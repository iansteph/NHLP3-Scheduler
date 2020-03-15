package iansteph.nhlp3.scheduler.model.scheduler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.scheduler.model.scheduler.game.Status;

import java.time.ZonedDateTime;
import java.util.Objects;

// The service calls to the NHL API endpoint will be serialized from JSON into this object. This allows for much easier access to the
// returned data, for only keeping the data that will be used (accomplished through the annotation to the class), etc.
//
// This class specifically is the representation of the inner "Game" data from the NHL Schedule API endpoint response
@JsonIgnoreProperties(ignoreUnknown = true)
public class Game {

    private int gamePk;
    private ZonedDateTime gameDate;
    private Status status;

    public Game() {}

    public Game(final int gamePk, final ZonedDateTime gameDate, final Status status) {

        this.gamePk = gamePk;
        this.gameDate = gameDate;
        this.status = status;
    }

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

    public Status getStatus() {

        return status;
    }

    public void setStatus(final Status status) {

        this.status = status;
    }

    @Override
    public String toString() {

        return "Game{" +
                "gamePk=" + gamePk +
                ", gameDate=" + gameDate +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return gamePk == game.gamePk &&
                Objects.equals(gameDate, game.gameDate) &&
                Objects.equals(status, game.status);
    }

    @Override
    public int hashCode() {

        return Objects.hash(gamePk, gameDate, status);
    }
}
