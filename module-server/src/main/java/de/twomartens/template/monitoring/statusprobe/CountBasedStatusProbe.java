package de.twomartens.template.monitoring.statusprobe;

import java.time.Clock;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.boot.actuate.health.Status;

public class CountBasedStatusProbe extends StatusProbe {

  private final AtomicInteger failureCount = new AtomicInteger(0);

  private final int maxFailureCount;

  public CountBasedStatusProbe(int maxFailureCount, Clock clock, StatusProbeCriticality criticality, String name,
      StatusProbeLogger statusProbeLogger) {
    super(clock, criticality, name, statusProbeLogger);
    this.maxFailureCount = maxFailureCount;
  }

  @Override
  protected synchronized void setStatus(Status status, Throwable throwable, String message) {
    if (status == Status.DOWN) {
      int failureCount = this.failureCount.incrementAndGet();
      if (failureCount > maxFailureCount) {
        super.setStatus(status, throwable, message);
      }
    } else if (status == Status.UP) {
      this.failureCount.set(0);
      super.setStatus(status, throwable, message);
    }
  }


}
