package de.twomartens.wahlrecht.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
@SecurityScheme(
    name = "oauth2",
    type = SecuritySchemeType.OAUTH2,
    flows = @OAuthFlows(
        implicit = @OAuthFlow(
            authorizationUrl = "https://id.2martens.de/realms/wahlrecht/protocol/openid-connect/auth",
            tokenUrl = "https://id.2martens.de/realms/wahlrecht/protocol/openid-connect/token"
        )
    )
)
@Configuration
public class OpenApiConfiguration {
  @Bean
  public OpenAPI customOpenAPI(@Value("${openapi.description}") String apiDesciption,
      @Value("${openapi.version}") String apiVersion, @Value("${openapi.title}") String apiTitle) {
    return new OpenAPI()
        .info(new io.swagger.v3.oas.models.info.Info()
            .title(apiTitle)
            .version(apiVersion)
            .description(apiDesciption));
  }
}
