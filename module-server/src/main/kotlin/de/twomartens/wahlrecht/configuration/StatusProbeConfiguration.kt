package de.twomartens.wahlrecht.configuration;

import de.twomartens.wahlrecht.monitoring.statusprobe.CountBasedStatusProbe;
import de.twomartens.wahlrecht.monitoring.statusprobe.StatusProbe;
import de.twomartens.wahlrecht.monitoring.statusprobe.StatusProbeCriticality;
import de.twomartens.wahlrecht.monitoring.statusprobe.StatusProbeLogger;
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