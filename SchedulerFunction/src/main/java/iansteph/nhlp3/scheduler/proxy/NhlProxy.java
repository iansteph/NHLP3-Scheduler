package iansteph.nhlp3.scheduler.proxy;

import iansteph.nhlp3.scheduler.client.NhlClient;
import iansteph.nhlp3.scheduler.model.scheduler.ScheduleResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;

import static com.google.common.base.Preconditions.checkNotNull;

// This class acts as a layer between the "low-level" service calls being made with the actual client and allows for additional application
// logic to be added in this layer. For instance, the objects used here could be different than in the client, we can add specific logic
// here that isn't a part of actually calling the NHL API endpoints, etc.
public class NhlProxy {

    final NhlClient nhlClient;

    private static final Logger logger = LogManager.getLogger(NhlProxy.class);

    public NhlProxy(final NhlClient nhlClient) {
        this.nhlClient = nhlClient;
    }

    public ScheduleResponse getScheduleForDate(final LocalDate date) {
        try {
            checkNotNull(date, "Date must be non-null when calling NhlProxy::getScheduleForDate");
            return nhlClient.getScheduleForDate(date);
        }
        catch (NullPointerException e) {
            logger.error(e);
            throw e;
        }
    }
}
