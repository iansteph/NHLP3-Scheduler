package iansteph.nhlp3.scheduler.client;

import iansteph.nhlp3.scheduler.UnitTestBase;
import iansteph.nhlp3.scheduler.model.scheduler.ScheduleResponse;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NhlClientTest extends UnitTestBase {

    @Test
    public void successfulGetScheduleForDate() {
        final RestTemplate mockRestTemplate = mock(RestTemplate.class);
        when(mockRestTemplate.getForObject(any(), any())).thenReturn(SCHEDULE_RESPONSE);
        final NhlClient nhlClient = new NhlClient(mockRestTemplate);

        final ScheduleResponse response = nhlClient.getScheduleForDate(LocalDate.now());

        assertThat(response, is(notNullValue()));
        assertThat(response.getDates(), is(notNullValue()));
        assertThat(response.getDates().size(), is(1));
    }
}
