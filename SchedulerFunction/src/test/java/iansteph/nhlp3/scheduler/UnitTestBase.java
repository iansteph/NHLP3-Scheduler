package iansteph.nhlp3.scheduler;

import com.google.common.collect.ImmutableList;
import iansteph.nhlp3.scheduler.model.scheduler.Date;
import iansteph.nhlp3.scheduler.model.scheduler.Game;
import iansteph.nhlp3.scheduler.model.scheduler.ScheduleResponse;

import java.time.*;
import java.util.List;

import static java.time.Month.OCTOBER;

public class UnitTestBase {

    protected static Game game = new Game(1, ZonedDateTime.of(LocalDateTime.of(LocalDate.of(2019, OCTOBER, 4),
            LocalTime.of(19, 30)), ZoneId.of("America/Los_Angeles")));
    protected static List<Game> games = ImmutableList.of(game);

    protected static Date date = new Date(games);
    protected static List<Date> dates = ImmutableList.of(date);

    protected static ScheduleResponse scheduleResponse = new ScheduleResponse(dates);
}
