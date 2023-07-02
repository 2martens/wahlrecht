package de.twomartens.wahlrecht.configuration;

import de.twomartens.wahlrecht.property.ServiceProperties;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ClockConfiguration {

  private final ServiceProperties serviceProperties;

  @Bean
  public Clock clock() {
    return Clock.system(serviceProperties.getDefaultTimeZone());
  }

}