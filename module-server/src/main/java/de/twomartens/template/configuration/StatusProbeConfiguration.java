package de.twomartens.template.configuration;

import de.twomartens.template.monitoring.statusprobe.CountBasedStatusProbe;
import de.twomartens.template.monitoring.statusprobe.StatusProbe;
import de.twomartens.template.monitoring.statusprobe.StatusProbeCriticality;
import de.twomartens.template.monitoring.statusprobe.StatusProbeLogger;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StatusProbeConfiguration {

  private final Clock clock;

  @Bean
  public StatusProbeLogger statusProbeLogger() {
    return new StatusProbeLogger(clock);
  }

  @Bean
  public StatusProbe testStatusProbe(StatusProbeLogger statusProbeLogger) {
    return new CountBasedStatusProbe(1,
        clock, StatusProbeCriticality.K1, "testStatusProbe", statusProbeLogger);
  }
}