package de.twomartens.template.configuration;

import de.twomartens.template.interceptor.HeaderInterceptorRest;
import de.twomartens.template.interceptor.LoggingInterceptorRest;
import de.twomartens.template.property.RestTemplateTimeoutProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

  @Bean
  public RestTemplate restTemplate(HeaderInterceptorRest headerInterceptorRest,
      LoggingInterceptorRest loggingInterceptor,
      RestTemplateTimeoutProperties restTemplateTimeoutProperties) {
    return new RestTemplateBuilder()
        .additionalInterceptors(headerInterceptorRest, loggingInterceptor)
        .setConnectTimeout(restTemplateTimeoutProperties.getConnectionRestTemplateTimeoutInMillis())
        .setReadTimeout(restTemplateTimeoutProperties.getReadTimeoutRestTemplateInMillis())
        .build();
  }

  @Bean
  public RestTemplate restTemplateRestHealthIndicator(HeaderInterceptorRest headerInterceptorRest,
      RestTemplateTimeoutProperties restTemplateTimeoutProperties) {
    return new RestTemplateBuilder()
        .additionalInterceptors(headerInterceptorRest)
        .setConnectTimeout(restTemplateTimeoutProperties.getConnectionRestHealthIndicatorTimeoutInMillis())
        .setReadTimeout(restTemplateTimeoutProperties.getReadTimeoutRestHealthIndicatorInMillis())
        .build();
  }
}
