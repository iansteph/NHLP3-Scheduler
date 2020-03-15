package iansteph.nhlp3.scheduler.model.scheduler.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {

    private String abstractGameState;
    private String codedGameState;
    private String detailedState;
    private String statusCode;
    private boolean startTimeTBD;

    public Status() {}

    public Status(
            final String abstractGameState,
            final String codedGameState,
            final String detailedState,
            final String statusCode,
            final boolean startTimeTBD
    ) {

        this.abstractGameState = abstractGameState;
        this.codedGameState = codedGameState;
        this.detailedState = detailedState;
        this.statusCode = statusCode;
        this.startTimeTBD = startTimeTBD;
    }

    public String getAbstractGameState() {

        return abstractGameState;
    }

    public void setAbstractGameState(final String abstractGameState) {

        this.abstractGameState = abstractGameState;
    }

    public String getCodedGameState() {

        return codedGameState;
    }

    public void setCodedGameState(final String codedGameState) {

        this.codedGameState = codedGameState;
    }

    public String getDetailedState() {

        return detailedState;
    }

    public void setDetailedState(final String detailedState) {

        this.detailedState = detailedState;
    }

    public String getStatusCode() {

        return statusCode;
    }

    public void setStatusCode(final String statusCode) {

        this.statusCode = statusCode;
    }

    public boolean isStartTimeTBD() {

        return startTimeTBD;
    }

    public void setStartTimeTBD(final boolean startTimeTBD) {

        this.startTimeTBD = startTimeTBD;
    }

    @Override
    public String toString() {

        return "Status{" +
                "abstractGameState='" + abstractGameState + '\'' +
                ", codedGameState='" + codedGameState + '\'' +
                ", detailedState='" + detailedState + '\'' +
                ", statusCode='" + statusCode + '\'' +
                ", startTimeTBD=" + startTimeTBD +
                '}';
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Status status = (Status) o;
        return startTimeTBD == status.startTimeTBD &&
                Objects.equals(abstractGameState, status.abstractGameState) &&
                Objects.equals(codedGameState, status.codedGameState) &&
                Objects.equals(detailedState, status.detailedState) &&
                Objects.equals(statusCode, status.statusCode);
    }

    @Override
    public int hashCode() {

        return Objects.hash(abstractGameState, codedGameState, detailedState, statusCode, startTimeTBD);
    }
}
