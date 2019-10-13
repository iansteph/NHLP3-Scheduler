package iansteph.nhlp3.scheduler.handler;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import iansteph.nhlp3.scheduler.UnitTestBase;
import iansteph.nhlp3.scheduler.client.NhlClient;
import iansteph.nhlp3.scheduler.proxy.NhlProxy;
import org.junit.Before;
import org.junit.Test;
import software.amazon.awssdk.services.cloudwatchevents.CloudWatchEventsClient;
import software.amazon.awssdk.services.cloudwatchevents.model.PutRuleRequest;
import software.amazon.awssdk.services.cloudwatchevents.model.PutRuleResponse;
import software.amazon.awssdk.services.cloudwatchevents.model.PutTargetsRequest;
import software.amazon.awssdk.services.cloudwatchevents.model.PutTargetsResponse;

import java.time.LocalDate;

public class SchedulerHandlerTest extends UnitTestBase {

    private NhlClient mockNhlClient = mock(NhlClient.class);
    private NhlProxy mockNhlProxy = new NhlProxy(mockNhlClient);
    private CloudWatchEventsClient mockCloudWatchEventsClient = mock(CloudWatchEventsClient.class);
    private DynamoDBMapper mockDynamoDbMapper = mock(DynamoDBMapper.class);

    @Before
    public void setMockNhlClient() {
        when(mockNhlClient.getScheduleForDate(any(LocalDate.class))).thenReturn(scheduleResponse);
        when(mockCloudWatchEventsClient.putRule(any(PutRuleRequest.class))).thenReturn(PutRuleResponse.builder().build());
        when(mockCloudWatchEventsClient.putTargets(any(PutTargetsRequest.class))).thenReturn(PutTargetsResponse.builder().build());
    }

    @Test
    public void successfulResponse() {
        final SchedulerHandler schedulerHandler = new SchedulerHandler(mockNhlProxy, mockCloudWatchEventsClient, mockDynamoDbMapper);
        final Object result = schedulerHandler.handleRequest(null, null);
        assertThat(result, is(notNullValue()));
    }
}
