package iansteph.nhlp3.scheduler.proxy;

import iansteph.nhlp3.scheduler.UnitTestBase;
import iansteph.nhlp3.scheduler.client.NhlClient;
import iansteph.nhlp3.scheduler.model.scheduler.ScheduleResponse;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NhlProxyTest extends UnitTestBase {

    private NhlClient mockNhlClient = mock(NhlClient.class);
    private NhlProxy nhlProxy = new NhlProxy(mockNhlClient);

    @Before
    public void setMockNhlClient() {
        when(mockNhlClient.getScheduleForDate(any())).thenReturn(SCHEDULE_RESPONSE);
    }

    @Test
    public void testGetScheduleForDateSuccessfullyReturnsScheduleForDate() {
        final ScheduleResponse response = nhlProxy.getScheduleForDate(LocalDate.now());
        assertThat(response, is(notNullValue()));
    }

    @Test(expected = NullPointerException.class)
    public void testNullDateThrowsNullPointerExceptionForGetScheduleForDate() {
        nhlProxy.getScheduleForDate(null);
    }
}
