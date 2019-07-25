package iansteph.nhlp3.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.ZonedDateTime;

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
