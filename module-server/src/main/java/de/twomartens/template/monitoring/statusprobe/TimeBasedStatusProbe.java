package de.twomartens.template.monitoring.statusprobe;

import java.time.Clock;
import java.time.Duration;
import java.time.ZonedDateTime;
import org.springframework.boot.actuate.health.Status;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

public class TimeBasedStatusProbe extends StatusProbe implements ScheduledStatusProbe {

  private final Clock clock;

  private final Duration maxFailureDuration;

  private ZonedDateTime lastSuccess;

  private Throwable throwable = null;

  private String message = null;

  public TimeBasedStatusProbe(Duration maxFailureDuration, Clock clock,
      ThreadPoolTaskScheduler threadPoolTaskScheduler, StatusProbeCriticality criticality, String name,
      StatusProbeLogger statusProbeLogger) {
    this(maxFailureDuration, clock, threadPoolTaskScheduler, Duration.ofMinutes(1), criticality, name,
        statusProbeLogger);
  }

  public TimeBasedStatusProbe(Duration maxFailureDuration, Clock clock,
      ThreadPoolTaskScheduler threadPoolTaskScheduler, Duration schedulePeriod, StatusProbeCriticality criticality,
      String name, StatusProbeLogger statusProbeLogger) {
    super(clock, criticality, name, statusProbeLogger);
    this.clock = clock;
    this.maxFailureDuration = maxFailureDuration;
    this.lastSuccess = null;
    scheduleTask(threadPoolTaskScheduler, schedulePeriod);
  }

  @Override
  protected synchronized void setStatus(Status status, Throwable throwable, String message) {
    if (status == Status.DOWN) {
      this.throwable = throwable;
      this.message = message;
    } else if (status == Status.UP) {
      lastSuccess = ZonedDateTime.now(clock);
      super.setStatus(status, throwable, message);
    }
  }

  private boolean isOverdue() {
    if (lastSuccess == null) {
      return false;
    }
    Duration timeSinceLastSuccess = Duration.between(lastSuccess, ZonedDateTime.now(clock));
    return maxFailureDuration.minus(timeSinceLastSuccess).isNegative();
  }

  public synchronized void runScheduledTask() {
    if (isOverdue()) {
      super.setStatus(Status.DOWN, throwable, message);
    }
  }


}
