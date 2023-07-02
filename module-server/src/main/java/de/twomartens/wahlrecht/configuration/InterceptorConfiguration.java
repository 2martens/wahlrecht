package de.twomartens.wahlrecht.configuration;

import de.twomartens.wahlrecht.interceptor.HeaderInterceptorRest;
import de.twomartens.wahlrecht.interceptor.LoggingInterceptorRest;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InterceptorConfiguration {

  @Bean
  public LoggingInterceptorRest loggingInterceptorRest(Clock clock) {
    return new LoggingInterceptorRest(clock);
  }

  @Bean
  public HeaderInterceptorRest headerInterceptorRest() {
    return new HeaderInterceptorRest();
  }

}
