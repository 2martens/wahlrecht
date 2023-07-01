package de.twomartens.template.monitoring.statusprobe;

import java.time.Duration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;

public interface ScheduledStatusProbe {

  void runScheduledTask();

  default void scheduleTask(ThreadPoolTaskScheduler threadPoolTaskScheduler,
      Duration schedulePeriod) {
    PeriodicTrigger periodicTrigger = new PeriodicTrigger(
        Duration.ofSeconds(schedulePeriod.toSeconds()));
    threadPoolTaskScheduler.schedule(this::runScheduledTask, periodicTrigger);
  }

}
