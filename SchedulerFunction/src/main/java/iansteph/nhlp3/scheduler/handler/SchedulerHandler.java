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
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.cloudwatchevents.CloudWatchEventsClient;
import software.amazon.awssdk.services.cloudwatchevents.model.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static java.lang.String.format;

/**
 * Handler for requests to Lambda function. This implements Lambda's RequestHandler interface which allows it to connect to Lambda.
 * When Lambda receives an invocation request this is the method that is called (this is set in the CloudFormation template for the
 * Lambda Function resource in the Properties section with the Handler setting). This is the entry point from AWS's Lambda service into the
 * NHLP3 Scheduler function code.
 */
public class SchedulerHandler implements RequestHandler<Object, Object> {

    private static final String EVENT_PUBLISHER_LAMBDA_FUNCTION_ARN = "arn:aws:lambda:us-east-1:627812672245:function:NHLP3-EventPublisher-Prod-EventPublisherFunction-1SBK7I88SVTNP";

    private CloudWatchEventsClient cloudWatchEventsClient;
    private DynamoDBMapper dynamoDbMapper;
    private NhlProxy nhlProxy;

    // This is the constructor used when the Lambda function is invoked
    public SchedulerHandler() {
        this.nhlProxy = new NhlProxy(new NhlClient(new RestTemplate()));
        final ApacheHttpClient.Builder httpClientBuilder = ApacheHttpClient.builder();
        this.cloudWatchEventsClient = CloudWatchEventsClient.builder()
                .httpClientBuilder(httpClientBuilder)
                .build();
        this.dynamoDbMapper = new DynamoDBMapper(AmazonDynamoDBClientBuilder.defaultClient());
    }

    SchedulerHandler(final NhlProxy nhlProxy, final CloudWatchEventsClient cloudWatchEventsClient, final DynamoDBMapper dynamoDbMapper) {
        this.cloudWatchEventsClient = cloudWatchEventsClient;
        this.dynamoDbMapper = dynamoDbMapper;
        this.nhlProxy = nhlProxy;
    }

    public Object handleRequest(final Object input, final Context context) {
        final LocalDate today = LocalDate.now(ZoneId.of("UTC"));
        System.out.println(format("Retrieving the NHL Schedule for %s", today));
        final ScheduleResponse scheduleResponseForDate = nhlProxy.getScheduleForDate(today);
        System.out.println(format("NHL Schedule API response: %s", scheduleResponseForDate));
        setEventProcessingForGames(scheduleResponseForDate.getDates());
        return scheduleResponseForDate;
    }

    private void setEventProcessingForGames(final List<Date> dates) {
        dates.stream()
                .map(Date::getGames)
                .forEach((games -> games.forEach(this::setEventProcessingForGame)));
    }

    private void setEventProcessingForGame(final Game game) {
        System.out.println(format("Start time for GameId %s is: %s", game.getGamePk(), game.getGameDate()));
        final String ruleName = createCloudWatchEventRule(game);
        addTargetToCloudWatchEventRule(ruleName, game);
        initializePlayByPlayProcessingRecord(game);
    }

    private String createCloudWatchEventRule(final Game game) {
        final String ruleName = format("GameId-%s", game.getGamePk());
        final PutRuleRequest putRuleRequest = PutRuleRequest.builder()
                .description(format("Event Rule triggering every minute invoking the play-by-play processor lambda for gameId: %s",
                        game.getGamePk()))
                .name(ruleName)
                .scheduleExpression(createCronExpressionForPutRuleRequest(game))
                .state(RuleState.ENABLED)
                .build();
        System.out.println(format("PutRuleRequest to CloudWatch Events API: %s", putRuleRequest));
        final PutRuleResponse putRuleResponse = cloudWatchEventsClient.putRule(putRuleRequest);
        System.out.println(format("PutRuleResponse from CloudWatch Events API: %s", putRuleResponse));
        return ruleName;
    }

    private String createCronExpressionForPutRuleRequest(final Game game) {
        final ZonedDateTime date = game.getGameDate();
        final int dayOfMonth = date.getDayOfMonth();
        final int month = date.getMonthValue();
        final int year = date.getYear();
        return format("cron(* * %s %s ? %s)", dayOfMonth, month, year);
    }

    private void addTargetToCloudWatchEventRule(final String ruleName, final Game game) {
        final Target target = Target.builder()
                .arn(EVENT_PUBLISHER_LAMBDA_FUNCTION_ARN)
                .input(format("{\"gamePk\":\"%s\"}", game.getGamePk()))
                .id("Event-Publisher-Lambda-Function")
                .build();
        final PutTargetsRequest putTargetsRequest = PutTargetsRequest.builder()
                .rule(ruleName)
                .targets(target)
                .build();
        System.out.println(format("PutTargetsRequest to CloudWatch Events API: %s", putTargetsRequest));
        final PutTargetsResponse putTargetsResponse = cloudWatchEventsClient.putTargets(putTargetsRequest);
        System.out.println(format("PutTargetsResponse from CloudWatch Events API: %s", putTargetsResponse));
    }

    private void initializePlayByPlayProcessingRecord(final Game game) {
        final NhlPlayByPlayProcessingItem initializedRecord = createPlayByPlayProcessingRecord(game);
        dynamoDbMapper.save(initializedRecord);
        System.out.println(format("Put NhlPlayByPlayProcessingItem in DynamoDB: %s", initializedRecord));

    }

    private NhlPlayByPlayProcessingItem createPlayByPlayProcessingRecord(final Game game) {
        final NhlPlayByPlayProcessingItem item = new NhlPlayByPlayProcessingItem();
        final String hashedGameId = Hashing.murmur3_128().hashInt(game.getGamePk()).toString();
        item.setCompositeGameId(String.format("%s~%s", hashedGameId, game.getGamePk()));
        item.setLastProcessedTimeStamp(transformLocalDateToInitialLastProcessedTimestamp(game.getGameDate().toLocalDate()));
        item.setLastProcessedEventIndex(0);
        item.setInIntermission(false);
        return item;
    }

    private String transformLocalDateToInitialLastProcessedTimestamp(final LocalDate localDate) {
        return format("%s%s%s_000000", localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth());
    }
}
