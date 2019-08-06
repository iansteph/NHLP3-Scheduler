package iansteph.nhlp3.scheduler.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import iansteph.nhlp3.scheduler.client.NhlClient;
import iansteph.nhlp3.scheduler.model.Date;
import iansteph.nhlp3.scheduler.model.Game;
import iansteph.nhlp3.scheduler.model.ScheduleResponse;
import iansteph.nhlp3.scheduler.proxy.NhlProxy;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutRuleRequest;
import software.amazon.awssdk.services.eventbridge.model.RuleState;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.lang.String.format;

/**
 * Handler for requests to Lambda function. This implements Lambda's RequestHandler interface which allows it to connect to Lambda.
 * When Lambda receives an invocation request this is the method that is called (this is set in the CloudFormation template for the
 * Lambda Function resource in the Properties section with the Handler setting). This is the entry point from AWS's Lambda service into the
 * NHLP3 Scheduler function code.
 */
public class SchedulerHandler implements RequestHandler<Object, Object> {

    private EventBridgeClient eventBridgeClient;
    private NhlProxy nhlProxy;

    public SchedulerHandler(final NhlProxy nhlProxy, final EventBridgeClient eventBridgeClient) {
        this.eventBridgeClient = eventBridgeClient;
        this.nhlProxy = nhlProxy;
    }

    public Object handleRequest(final Object input, final Context context) {
        if (nhlProxy == null) {
            nhlProxy = new NhlProxy(new NhlClient());
        }
        if (eventBridgeClient == null) {
            eventBridgeClient = EventBridgeClient.create();
        }

        final ScheduleResponse scheduleResponseForDate = nhlProxy.getScheduleForDate(LocalDate.parse("2018-01-09",
                DateTimeFormatter.ISO_LOCAL_DATE));
        System.out.println(scheduleResponseForDate);

        final ScheduleResponse scheduleResponseForDateRange = nhlProxy.getScheduleForDateRange(LocalDate.parse("2018-01-11",
                DateTimeFormatter.ISO_LOCAL_DATE), LocalDate.parse("2018-01-12", DateTimeFormatter.ISO_LOCAL_DATE));
        System.out.println(scheduleResponseForDateRange);

        setEventProcessingForGames(scheduleResponseForDate.getDates());
        setEventProcessingForGames(scheduleResponseForDateRange.getDates());

        return new Object();
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
                .name(format("GameId:%s", game.getGamePk()))
                .scheduleExpression(createCronExpressionForPutRuleRequest(game))
                .state(RuleState.ENABLED)
                .build();
        eventBridgeClient.putRule(putRuleRequest);
    }

    private String createCronExpressionForPutRuleRequest(final Game game) {
        final ZonedDateTime date = game.getGameDate();
        final int dayOfMonth = date.getDayOfMonth();
        final int month = date.getMonthValue();
        final int year = date.getYear();
        return format("cron(0/60 * * %s %s %s)", dayOfMonth, month, year);
    }
}
