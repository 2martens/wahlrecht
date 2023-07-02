package de.twomartens.wahlrecht.monitoring.statusprobe;

import java.time.Clock;
import java.time.ZonedDateTime;
import lombok.Getter;
import org.springframework.boot.actuate.health.Status;

@Getter
public class StatusProbe {

  private final Clock clock;

  private Status status = Status.UP;
  private Throwable throwable = null;
  private String message = null;
  private ZonedDateTime lastStatusChange;
  private final StatusProbeLogger statusProbeLogger;
  private final String name;

  public StatusProbe(Clock clock, StatusProbeCriticality criticality, String name,
      StatusProbeLogger statusProbeLogger) {
    this.clock = clock;
    this.name = name;
    this.statusProbeLogger = statusProbeLogger;
    statusProbeLogger.registerStatusProbe(name, criticality);
  }

  protected void setStatus(Status status, Throwable throwable, String message) {
    if (status != this.status) {
      lastStatusChange = ZonedDateTime.now(clock);
      statusProbeLogger.logStatusChange(name, message, status, lastStatusChange, throwable);
    }
    this.status = status;
    this.throwable = throwable;
    this.message = message;
  }

  public void up() {
    setStatus(Status.UP, null, null);
  }

  public void up(String message) {
    setStatus(Status.UP, null, message);
  }

  public void down() {
    setStatus(Status.DOWN, null, null);
  }

  public void down(Throwable throwable) {
    setStatus(Status.DOWN, throwable, null);
  }

  public void down(String message) {
    setStatus(Status.DOWN, null, message);
  }

  protected void down(Throwable throwable, String message) {
    setStatus(Status.DOWN, throwable, message);
  }

}
