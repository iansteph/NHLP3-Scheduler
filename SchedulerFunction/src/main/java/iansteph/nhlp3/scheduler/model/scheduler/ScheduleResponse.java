package iansteph.nhlp3.scheduler.model.scheduler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

// The service calls to the NHL API endpoint will be serialized from JSON into this object. This allows for much easier access to the
// returned data, for only keeping the data that will be used (accomplished through the annotation to the class), etc.
//
// This class specifically is the representation of the top-level data returned from the NHL Schedule API endpoint
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleResponse {

    private List<Date> dates;

    public ScheduleResponse() {}

    public ScheduleResponse(final List<Date> dates) {
        this.dates = dates;
    }

    public List<Date> getDates() {
        return dates;
    }

    public void setDates(final List<Date> dates) {
        this.dates = dates;
    }

    public String toString() {
        return "ScheduleResponse(dates=" + dates + ")";
    }
}
