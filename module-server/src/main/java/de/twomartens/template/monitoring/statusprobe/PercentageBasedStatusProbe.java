package de.twomartens.template.monitoring.statusprobe;

import java.time.Clock;
import java.time.Duration;
import org.springframework.boot.actuate.health.Status;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * uses the percentage of down statuses within a given period (default: 1 min) to determine if status of probe is down.
 * this is meant to be used to avoid flickering status probes on services that have lots of status updates. When there
 * is no significant amount of requests during one scheduling period, the behavior may be arbitrary.
 */
public class PercentageBasedStatusProbe extends StatusProbe implements ScheduledStatusProbe {

  private final int maxFailurePercent;

  private int requestCount = 0;
  private int downCount = 0;
  private Throwable throwable;
  private String message;

  public PercentageBasedStatusProbe(int maxFailurePercent, Clock clock,
      ThreadPoolTaskScheduler threadPoolTaskScheduler, StatusProbeCriticality criticality, String name,
      StatusProbeLogger statusProbeLogger) {
    this(maxFailurePercent, clock, threadPoolTaskScheduler, Duration.ofMinutes(1), criticality, name,
        statusProbeLogger);
  }

  public PercentageBasedStatusProbe(int maxFailurePercent, Clock clock,
      ThreadPoolTaskScheduler threadPoolTaskScheduler, Duration schedulePeriod, StatusProbeCriticality criticality,
      String name, StatusProbeLogger statusProbeLogger) {
    super(clock, criticality, name, statusProbeLogger);
    this.maxFailurePercent = maxFailurePercent;
    scheduleTask(threadPoolTaskScheduler, schedulePeriod);
  }

  @Override
  protected synchronized void setStatus(Status status, Throwable throwable, String message) {
    if (status == Status.DOWN) {
      downCount++;
      this.throwable = throwable;
      this.message = message;
    }
    requestCount++;
  }

  private void reset() {
    requestCount = 0;
    downCount = 0;
    throwable = null;
    message = null;
  }

  public synchronized void runScheduledTask() {
    if (requestCount > 0 && (downCount * 100.0 / requestCount) > maxFailurePercent) {
      super.setStatus(Status.DOWN, throwable, message);
    } else {
      super.setStatus(Status.UP, null, null);
    }
    reset();
  }

}
