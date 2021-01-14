package iansteph.nhlp3.scheduler.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.common.hash.Hashing;
import iansteph.nhlp3.scheduler.client.NhlClient;
import iansteph.nhlp3.scheduler.model.dynamo.NhlPlayByPlayProcessingItem;
import iansteph.nhlp3.scheduler.model.scheduler.Date;
import iansteph.nhlp3.scheduler.model.scheduler.Game;
import iansteph.nhlp3.scheduler.model.scheduler.ScheduleResponse;
import iansteph.nhlp3.scheduler.proxy.NhlProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.cloudwatchevents.CloudWatchEventsClient;
import software.amazon.awssdk.services.cloudwatchevents.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

/**
 * Handler for requests to Lambda function. This implements Lambda's RequestHandler interface which allows it to connect to Lambda.
 * When Lambda receives an invocation request this is the method that is called (this is set in the CloudFormation template for the
 * Lambda Function resource in the Properties section with the Handler setting). This is the entry point from AWS's Lambda service into the
 * NHLP3 Scheduler function code.
 */
public class SchedulerHandler implements RequestHandler<Object, Object> {

    private CloudWatchEventsClient cloudWatchEventsClient;
    private DynamoDbClient dynamoDbClient;
    private DynamoDBMapper dynamoDbMapper;
    private NhlProxy nhlProxy;

    private static final String EVENT_PUBLISHER_LAMBDA_FUNCTION_ARN = "arn:aws:lambda:us-east-1:627812672245:function:NHLP3-EventPublisher-prod";
    private static final String SHIFT_PUBLISHER_LAMBDA_FUNCTION_ARN = "arn:aws:lambda:us-east-1:627812672245:function:NHLP3-ShiftPublisher-prod-ShiftPublisherFunction-1SUGFQ7ZP1H8P";
    private static final Logger logger = LogManager.getLogger(SchedulerHandler.class);

    // This is the constructor used when the Lambda function is invoked
    public SchedulerHandler() {
        this.nhlProxy = new NhlProxy(new NhlClient(new RestTemplate()));
        final ApacheHttpClient.Builder httpClientBuilder = ApacheHttpClient.builder();
        this.cloudWatchEventsClient = CloudWatchEventsClient.builder()
                .httpClientBuilder(httpClientBuilder)
                .build();
        this.dynamoDbClient = DynamoDbClient.create();
        this.dynamoDbMapper = new DynamoDBMapper(AmazonDynamoDBClientBuilder.defaultClient());
    }

    SchedulerHandler(
            final NhlProxy nhlProxy,
            final CloudWatchEventsClient cloudWatchEventsClient,
            final DynamoDBMapper dynamoDbMapper,
            final DynamoDbClient dynamoDbClient
    ) {
        this.nhlProxy = nhlProxy;
        this.cloudWatchEventsClient = cloudWatchEventsClient;
        this.dynamoDbMapper = dynamoDbMapper;
        this.dynamoDbClient = dynamoDbClient;
    }

    public Object handleRequest(final Object input, final Context context) {
        final LocalDate today = LocalDate.now(ZoneId.of("UTC"));
        logger.info(format("Retrieving the NHL Schedule for %s", today));
        final ScheduleResponse scheduleResponseForDate = nhlProxy.getScheduleForDate(today);
        logger.info(format("NHL Schedule API response: %s", scheduleResponseForDate));
        setEventProcessingForGames(scheduleResponseForDate.getDates());
        return scheduleResponseForDate;
    }

    private void setEventProcessingForGames(final List<Date> dates) {
        dates.stream()
                .map(Date::getGames)
                .map(games -> games.stream()
                        .filter(game -> !game.getStatus().getDetailedState().toLowerCase().equals("postponed"))
                )
                .forEach((games -> games.forEach(this::setEventProcessingForGame)));
    }

    private void setEventProcessingForGame(final Game game) {
        logger.info(format("Start time for GameId %s is: %s", game.getGamePk(), game.getGameDate()));
        final String ruleName = createCloudWatchEventRule(game);
        addTargetToCloudWatchEventRule(ruleName, game);
        initializePlayByPlayProcessingRecord(game);
    }

    private String createCloudWatchEventRule(final Game game) {
        final String ruleName = format("GameId-%s", game.getGamePk());
        final PutRuleRequest putRuleRequest = PutRuleRequest.builder()
                .description(format("Event Rule triggering every minute invoking the play-by-play processor lambda for gameId: %s", game.getGamePk()))
                .name(ruleName)
                .scheduleExpression("rate(1 minute)")
                .state(RuleState.ENABLED)
                .build();
        logger.info(format("PutRuleRequest to CloudWatch Events API: %s", putRuleRequest));
        final PutRuleResponse putRuleResponse = cloudWatchEventsClient.putRule(putRuleRequest);
        logger.info(format("PutRuleResponse from CloudWatch Events API: %s", putRuleResponse));
        return ruleName;
    }

    private void addTargetToCloudWatchEventRule(final String ruleName, final Game game) {
        final String targetInput = format("{\"gameId\":\"%s\"}", game.getGamePk());
        final Target eventPublisherTarget = Target.builder()
                .arn(EVENT_PUBLISHER_LAMBDA_FUNCTION_ARN)
                .input(targetInput)
                .id("Event-Publisher-Lambda-Function")
                .build();
        final Target shiftPublisherTarget = Target.builder()
                .arn(SHIFT_PUBLISHER_LAMBDA_FUNCTION_ARN)
                .input(targetInput)
                .id("Shift-Publisher-Lambda-Function")
                .build();
        final PutTargetsRequest putTargetsRequest = PutTargetsRequest.builder()
                .rule(ruleName)
                .targets(eventPublisherTarget, shiftPublisherTarget)
                .build();
        logger.info(format("PutTargetsRequest to CloudWatch Events API: %s", putTargetsRequest));
        final PutTargetsResponse putTargetsResponse = cloudWatchEventsClient.putTargets(putTargetsRequest);
        logger.info(format("PutTargetsResponse from CloudWatch Events API: %s", putTargetsResponse));
    }

    private void initializePlayByPlayProcessingRecord(final Game game) {
        // Initialize record for Play-by-Play processing
        final NhlPlayByPlayProcessingItem initializedRecord = createPlayByPlayProcessingRecord(game);
        dynamoDbMapper.save(initializedRecord);
        logger.info(format("Put NhlPlayByPlayProcessingItem in DynamoDB: %s", initializedRecord));

        // Initialize record for shift publishing
        createShiftPublishingRecord(game);
    }

    private NhlPlayByPlayProcessingItem createPlayByPlayProcessingRecord(final Game game) {
        final NhlPlayByPlayProcessingItem item = new NhlPlayByPlayProcessingItem();
        final String hashedGameId = Hashing.murmur3_128().hashInt(game.getGamePk()).toString();
        item.setCompositeGameId(String.format("%s~%s", hashedGameId, game.getGamePk()));
        item.setLastProcessedEventIndex(0);
        return item;
    }

    private void createShiftPublishingRecord(final Game game) {

        final String key = format("SHIFTPUBLISHING#%d", game.getGamePk());
        final AttributeValue keyAttribute = AttributeValue.builder().s(key).build();
        final Map<String, AttributeValue> item = new HashMap<>();
        item.put("PK", keyAttribute);
        item.put("SK", keyAttribute);
        final Map<String, AttributeValue> shiftPublishingAttribute = new HashMap<>();
        shiftPublishingAttribute.put("visitor", AttributeValue.builder().m(Collections.emptyMap()).build());
        shiftPublishingAttribute.put("home", AttributeValue.builder().m(Collections.emptyMap()).build());
        item.put("shiftPublishingRecord", AttributeValue.builder().m(shiftPublishingAttribute).build());
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName("NHLP3-Aggregate")
                .item(item)
                .build();
        logger.info(format("Putting ShiftPublishingItem in DynamoDB: %s", putItemRequest));
        final PutItemResponse putItemResponse = dynamoDbClient.putItem(putItemRequest);
    }
}
