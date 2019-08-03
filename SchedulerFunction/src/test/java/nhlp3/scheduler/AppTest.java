package nhlp3.scheduler;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class AppTest {

  @Test
  public void successfulResponse() {
    final App app = new App();
    final Object result = app.handleRequest(null, null);
    assertNotNull(result);
  }
}
