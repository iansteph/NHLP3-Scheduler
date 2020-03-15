package iansteph.nhlp3.scheduler.model.scheduler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Objects;

// The service calls to the NHL API endpoint will be serialized from JSON into this object. This allows for much easier access to the
// returned data, for only keeping the data that will be used (accomplished through the annotation to the class), etc.
//
// This class specifically is the representation of the inner "Date" data from the NHL Schedule API endpoint response
@JsonIgnoreProperties(ignoreUnknown = true)
public class Date {

    private List<Game> games;

    public Date() {}

    public Date(final List<Game> games) {

        this.games = games;
    }

    public List<Game> getGames() {

        return games;
    }

    public void setGames(final List<Game> games) {

        this.games = games;
    }

    @Override
    public String toString() {

        return "Date{" +
                "games=" + games +
                '}';
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Date date = (Date) o;
        return Objects.equals(games, date.games);
    }

    @Override
    public int hashCode() {

        return Objects.hash(games);
    }
}
