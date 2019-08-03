package iansteph.nhlp3.scheduler.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import iansteph.nhlp3.scheduler.client.NhlClient;
import iansteph.nhlp3.scheduler.model.ScheduleResponse;
import iansteph.nhlp3.scheduler.proxy.NhlProxy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Handler for requests to Lambda function.
 */
public class SchedulerHandler implements RequestHandler<Object, Object> {

    public Object handleRequest(final Object input, final Context context) {
        final NhlProxy nhlProxy = new NhlProxy(new NhlClient());

        final ScheduleResponse scheduleResponseForDate = nhlProxy.getScheduleForDate(LocalDate.parse("2018-01-09",
                DateTimeFormatter.ISO_LOCAL_DATE));
        System.out.println(scheduleResponseForDate);

        final ScheduleResponse scheduleResponseForDateRange = nhlProxy.getScheduleForDateRange(LocalDate.parse("2018-01-11",
                DateTimeFormatter.ISO_LOCAL_DATE), LocalDate.parse("2018-01-12", DateTimeFormatter.ISO_LOCAL_DATE));
        System.out.println(scheduleResponseForDateRange);

        return new Object();
    }
}
