package iansteph.nhlp3.scheduler.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import iansteph.nhlp3.scheduler.client.NhlClient;
import iansteph.nhlp3.scheduler.model.Date;
import iansteph.nhlp3.scheduler.model.Game;
import iansteph.nhlp3.scheduler.model.ScheduleResponse;
import iansteph.nhlp3.scheduler.proxy.NhlProxy;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.cloudwatchevents.CloudWatchEventsClient;
import software.amazon.awssdk.services.cloudwatchevents.model.PutRuleRequest;
import software.amazon.awssdk.services.cloudwatchevents.model.PutRuleResponse;
import software.amazon.awssdk.services.cloudwatchevents.model.RuleState;

import java.time.LocalDate;
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

    private CloudWatchEventsClient cloudWatchEventsClient;
    private NhlProxy nhlProxy;

    // This is the constructor used when the Lambda function is invoked
    public SchedulerHandler() {}

    SchedulerHandler(final NhlProxy nhlProxy, final CloudWatchEventsClient cloudWatchEventsClient) {
        this.cloudWatchEventsClient = cloudWatchEventsClient;
        this.nhlProxy = nhlProxy;
    }

    public Object handleRequest(final Object input, final Context context) {
        if (nhlProxy == null) {
            nhlProxy = new NhlProxy(new NhlClient());
        }
        if (cloudWatchEventsClient == null) {
            final ApacheHttpClient.Builder httpClientBuilder = ApacheHttpClient.builder();
            cloudWatchEventsClient = CloudWatchEventsClient.builder()
                    .httpClientBuilder(httpClientBuilder)
                    .build();
        }
        final ScheduleResponse scheduleResponseForDate = nhlProxy.getScheduleForDate(LocalDate.now());
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
        final PutRuleRequest putRuleRequest = PutRuleRequest.builder()
                .description(format("Event Rule triggering every minute invoking the play-by-play processor lambda for gameId: %s",
                        game.getGamePk()))
                .name(format("GameId-%s", game.getGamePk()))
                .scheduleExpression(createCronExpressionForPutRuleRequest(game))
                .state(RuleState.ENABLED)
                .build();
        System.out.println(format("PutRuleRequest to CloudWatch Events API: %s", putRuleRequest));
        final PutRuleResponse putRuleResponse = cloudWatchEventsClient.putRule(putRuleRequest);
        System.out.println(format("PutRuleResponse from CloudWatch Events API: %s", putRuleResponse));
    }

    private String createCronExpressionForPutRuleRequest(final Game game) {
        final ZonedDateTime date = game.getGameDate();
        final int dayOfMonth = date.getDayOfMonth();
        final int month = date.getMonthValue();
        final int year = date.getYear();
        return format("cron(* * %s %s ? %s)", dayOfMonth, month, year);
    }
}
