package iansteph.nhlp3.scheduler.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.ZonedDateTime;

// The service calls to the NHL API endpoint will be serialized from JSON into this object. This allows for much easier access to the
// returned data, for only keeping the data that will be used (accomplished through the annotation to the class), etc.
//
// This class specifically is the representation of the inner "Game" data from the NHL Schedule API endpoint response
@JsonIgnoreProperties(ignoreUnknown = true)
public class Game {

    private int gamePk;

    private ZonedDateTime gameDate;

    public int getGamePk() {
        return gamePk;
    }

    public void setGamePk(final int gamePk) {
        this.gamePk = gamePk;
    }

    public ZonedDateTime getGameDate() {
        return gameDate;
    }

    public void setGameDate(final ZonedDateTime gameDate) {
        this.gameDate = gameDate;
    }

    public String toString() {
        return "Game(gamePk=" + gamePk + ",gameDate=" + gameDate + ")";
    }
}
