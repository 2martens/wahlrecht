package de.twomartens.wahlrecht;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableMongoAuditing
@EnableScheduling
@SpringBootApplication
public class MainApplication extends SpringBootServletInitializer {

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    return builder.sources(MainApplication.class);
  }

  public static void main(String[] args) {
    SpringApplication.run(MainApplication.class, args);
  }

  @Bean
  public OpenAPI customOpenAPI(@Value("${openapi.description}") String apiDesciption,
      @Value("${openapi.version}") String apiVersion, @Value("${openapi.title}") String apiTitle) {
    return new OpenAPI()
        .info(new Info()
            .title(apiTitle)
            .version(apiVersion)
            .description(apiDesciption));
  }
}
