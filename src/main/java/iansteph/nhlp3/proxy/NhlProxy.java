package iansteph.nhlp3.proxy;

import iansteph.nhlp3.client.NhlClient;
import iansteph.nhlp3.model.ScheduleResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class NhlProxy {

    final NhlClient nhlClient;

    public NhlProxy(final NhlClient nhlClient) {
        this.nhlClient = nhlClient;
    }

    public ScheduleResponse getScheduleForDate(final LocalDate date) {
        checkNotNull(date);
        return nhlClient.getScheduleForDate(date);
    }

    public ScheduleResponse getScheduleForDateRange(final LocalDate startDate, final LocalDate endDate) {
        checkNotNull(startDate);
        checkNotNull(endDate);
        checkArgument(startDate.isBefore(endDate));
        return nhlClient.getScheduleForDateRange(startDate, endDate);
    }

    // Temporary way to call this before I implement it in lambda
    public static void main(String[] args) {
        final NhlProxy nhlProxy = new NhlProxy(new NhlClient());

        final ScheduleResponse scheduleResponseForDate = nhlProxy.getScheduleForDate(LocalDate.parse("2018-01-09",
                DateTimeFormatter.ISO_LOCAL_DATE));
        System.out.println(scheduleResponseForDate);

        final ScheduleResponse scheduleResponseForDateRange = nhlProxy.getScheduleForDateRange(LocalDate.parse("2018-01-11",
                DateTimeFormatter.ISO_LOCAL_DATE), LocalDate.parse("2018-01-12", DateTimeFormatter.ISO_LOCAL_DATE));
        System.out.println(scheduleResponseForDateRange);
    }
}
