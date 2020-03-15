package iansteph.nhlp3.scheduler;

import com.google.common.collect.ImmutableList;
import iansteph.nhlp3.scheduler.model.scheduler.Date;
import iansteph.nhlp3.scheduler.model.scheduler.Game;
import iansteph.nhlp3.scheduler.model.scheduler.ScheduleResponse;
import iansteph.nhlp3.scheduler.model.scheduler.game.Status;

import java.time.*;
import java.util.List;

import static java.time.Month.OCTOBER;

public class UnitTestBase {

    protected final static ScheduleResponse SCHEDULE_RESPONSE = generateScheduleResponse();

    protected static Game generateGame() {

        return generateGameWithDetailedState("some");
    }

    protected static Game generateGameWithDetailedState(final String detailedState) {

        final Status status = new Status(null, null, detailedState, null ,false);
        final LocalDate localDate = LocalDate.of(2019, OCTOBER, 4);
        final LocalTime localTime = LocalTime.of(19, 30);
        final LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        final ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        final ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
        return new Game(1, zonedDateTime, status);
    }

    protected static ScheduleResponse generateScheduleResponse() {

        final List<Game> games = ImmutableList.of(generateGame());
        final List<Date> dates = ImmutableList.of(new Date(games));
        return new ScheduleResponse(dates);
    }

    protected static ScheduleResponse generateScheduleResponseWithGame(final Game game) {

        final List<Game> games = ImmutableList.of(game);
        final List<Date> dates = ImmutableList.of(new Date(games));
        return new ScheduleResponse(dates);
    }
}
