package iansteph.nhlp3.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleResponse {

    private List<Date> dates;
    private int totalGames;

    public List<Date> getDates() {
        return dates;
    }

    public void setDates(final List<Date> dates) {
        this.dates = dates;
    }

    public int getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(final int totalGames) {
        this.totalGames = totalGames;
    }

    public String toString() {
        return "ScheduleResponse(totalGames=" + totalGames + ",dates=" + dates + ")";
    }
}
