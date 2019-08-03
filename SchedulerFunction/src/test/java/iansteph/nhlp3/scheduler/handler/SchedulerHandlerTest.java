package iansteph.nhlp3.scheduler.handler;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class SchedulerHandlerTest {

  @Test
  public void successfulResponse() {
    final SchedulerHandler schedulerHandler = new SchedulerHandler();
    final Object result = schedulerHandler.handleRequest(null, null);
    assertNotNull(result);
  }
}
