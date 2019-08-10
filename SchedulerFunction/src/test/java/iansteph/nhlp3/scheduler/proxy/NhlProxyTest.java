package iansteph.nhlp3.scheduler.proxy;

import iansteph.nhlp3.scheduler.client.NhlClient;
import iansteph.nhlp3.scheduler.model.ScheduleResponse;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NhlProxyTest {

    private NhlClient mockNhlClient = mock(NhlClient.class);
    private NhlProxy nhlProxy = new NhlProxy(mockNhlClient);

    @Before
    public void setMockNhlClient() {
        when(mockNhlClient.getScheduleForDate(any())).thenReturn(new ScheduleResponse());
    }

    @Test
    public void testGetScheduleForDateSuccessfullyReturnsScheduleForDate() {
        final ScheduleResponse response = nhlProxy.getScheduleForDate(LocalDate.now());
        assertNotNull(response);
    }

    @Test(expected = NullPointerException.class)
    public void testNullDateThrowsNullPointerExceptionForGetScheduleForDate() {
        nhlProxy.getScheduleForDate(null);
    }
}
