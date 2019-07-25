package iansteph.nhlp3.client;

import iansteph.nhlp3.model.ScheduleResponse;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NhlClient {

    private final static RestTemplate restTemplate = new RestTemplate();
    private final static String BASE_NHL_URL = "http://statsapi.web.nhl.com/api/v1/";
    private final static String SCHEDULE_API = "schedule";

    public ScheduleResponse getScheduleForDate(final LocalDate date) {
        final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(BASE_NHL_URL + SCHEDULE_API)
                .queryParam("date", DateTimeFormatter.ISO_LOCAL_DATE.format(date));
        return restTemplate.getForObject(uriComponentsBuilder.toUriString(), ScheduleResponse.class);
    }

    public ScheduleResponse getScheduleForDateRange(final LocalDate startDate, final LocalDate endDate) {
        final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(BASE_NHL_URL + SCHEDULE_API)
                .queryParam("startDate", DateTimeFormatter.ISO_LOCAL_DATE.format(startDate))
                .queryParam("endDate", DateTimeFormatter.ISO_LOCAL_DATE.format(endDate));
        return restTemplate.getForObject(uriComponentsBuilder.toUriString(), ScheduleResponse.class);
    }
}
