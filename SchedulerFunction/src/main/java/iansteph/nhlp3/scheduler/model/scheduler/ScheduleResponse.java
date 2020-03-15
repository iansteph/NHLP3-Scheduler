package iansteph.nhlp3.scheduler.model.scheduler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Objects;

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

    @Override
    public String toString() {

        return "ScheduleResponse{" +
                "dates=" + dates +
                '}';
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleResponse that = (ScheduleResponse) o;
        return Objects.equals(dates, that.dates);
    }

    @Override
    public int hashCode() {

        return Objects.hash(dates);
    }
}
