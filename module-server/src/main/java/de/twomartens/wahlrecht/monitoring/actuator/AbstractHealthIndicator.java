package de.twomartens.wahlrecht.monitoring.actuator;

import java.io.Closeable;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.slf4j.MDC.MDCCloseable;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractHealthIndicator implements HealthIndicator {

  public static final String HOST = "localhost";
  public static final String HTTP_PREFIX = "http://";
  public static final String HOST_PORT_SEPERATOR = ":";
  public static final String PATH_SEPERATOR = "/";
  public static final String PARAMETER_SEPERATOR = "?";
  public static final String DETAIL_ENDPOINT_KEY = "endpoint";

  private final String logStatusDownMessage = String.format("health indicator '%s' invoked with status '%s'",
      indicatorName(), Status.DOWN.getCode());
  private final String logStatusUpMessage = String.format("health indicator '%s' invoked with status '%s'",
      indicatorName(), Status.UP.getCode());
  private final Clock clock;
  private final Preparable preparable;
  private boolean firstTime = true;

  /**
   * main method that determines the health of the service
   */
  protected abstract Health determineHealth();

  @Override
  public Health health() {
    try (Closeable ignored = preparable.prepare()) {
      Health result = null;
      Exception exception = null;
      long start = clock.millis();
      try {
        result = determineHealth();
      } catch (RuntimeException e) {
        exception = e;
        result = Health.down().withException(e).build();
      } finally {
        logInvocation(result, exception, start, clock.millis());
      }
      return result;
    } catch (IOException e) {
      log.error("unexpected exception occurred", e);
      return Health.down(e).build();
    }
  }

  private void logInvocation(Health health, Exception exception, long start, long end) {
    Duration duration = Duration.ofMillis(end - start);
    try (MDCCloseable ignored = MDC.putCloseable("event.duration", Long.toString(duration.toNanos()))) {
      if (exception != null || health == null) {
        log.error(logStatusDownMessage, exception);
        firstTime = true;
      } else if (health.getStatus() == Status.DOWN) {
        log.warn(logStatusDownMessage);
        firstTime = true;
      } else if (firstTime) {
        log.info(logStatusUpMessage);
        firstTime = false;
      } else {
        log.trace(logStatusUpMessage);
      }
    }
  }

  private String indicatorName() {
    return this.getClass().getSimpleName()
        .replace("HealthIndicator", "")
        .toLowerCase();
  }

}
