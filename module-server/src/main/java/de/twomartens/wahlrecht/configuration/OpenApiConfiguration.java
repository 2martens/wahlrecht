package de.twomartens.wahlrecht.configuration;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

  @Bean
  public GroupedOpenApi swaggerApi10() {
    return GroupedOpenApi.builder()
        .group("1.0")
        .pathsToMatch("/wahlrecht/v1/**")
        .build();
  }

}
