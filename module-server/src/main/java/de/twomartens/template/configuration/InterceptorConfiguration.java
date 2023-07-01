package de.twomartens.template.configuration;

import de.twomartens.template.interceptor.HeaderInterceptorRest;
import de.twomartens.template.interceptor.LoggingInterceptorRest;
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
