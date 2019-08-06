package iansteph.nhlp3.scheduler.handler;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import iansteph.nhlp3.scheduler.client.NhlClient;
import iansteph.nhlp3.scheduler.model.Date;
import iansteph.nhlp3.scheduler.model.Game;
import iansteph.nhlp3.scheduler.model.ScheduleResponse;
import iansteph.nhlp3.scheduler.proxy.NhlProxy;
import org.junit.Before;
import org.junit.Test;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutRuleRequest;
import software.amazon.awssdk.services.eventbridge.model.PutRuleResponse;

import java.time.ZonedDateTime;

public class SchedulerHandlerTest {

    private NhlClient mockNhlClient = mock(NhlClient.class);
    private NhlProxy mockNhlProxy = new NhlProxy(mockNhlClient);
    private EventBridgeClient mockEventBridgeClient = mock(EventBridgeClient.class);

    @Before
    public void setMockNhlClient() {
        final Game game1 = new Game();
        game1.setGameDate(ZonedDateTime.now());
        game1.setGamePk(0);

        final Game game2 = new Game();
        game2.setGamePk(0);
        game2.setGameDate(ZonedDateTime.now());

        final Date date1 = new Date();
        date1.setGames(ImmutableList.of(game1));

        final Date date2 = new Date();
        date2.setGames(ImmutableList.of(game2));

        final ScheduleResponse scheduleResponse = new ScheduleResponse();
        scheduleResponse.setDates(ImmutableList.of(date1));

        final ScheduleResponse scheduleResponseForDateRange = new ScheduleResponse();
        scheduleResponseForDateRange.setDates(ImmutableList.of(date1, date2));

        when(mockNhlClient.getScheduleForDate(any())).thenReturn(scheduleResponse);
        when(mockNhlClient.getScheduleForDateRange(any(), any())).thenReturn(scheduleResponseForDateRange);
        when(mockEventBridgeClient.putRule(any(PutRuleRequest.class))).thenReturn(PutRuleResponse.builder().build());
    }

    @Test
    public void successfulResponse() {
        final SchedulerHandler schedulerHandler = new SchedulerHandler(mockNhlProxy, mockEventBridgeClient);
        final Object result = schedulerHandler.handleRequest(null, null);
        assertNotNull(result);
    }
}
