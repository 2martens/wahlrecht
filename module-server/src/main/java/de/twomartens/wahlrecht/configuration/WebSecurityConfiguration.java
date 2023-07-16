package de.twomartens.wahlrecht.configuration;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.keycloak.adapters.authorization.integration.jakarta.ServletPolicyEnforcerFilter;
import org.keycloak.adapters.authorization.spi.ConfigurationResolver;
import org.keycloak.adapters.authorization.spi.HttpRequest;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig.EnforcementMode;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig.PathConfig;
import org.keycloak.util.JsonSerialization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

  private static final Collection<String> PERMITTED_PATHS = List.of(
      "/wahlrecht/v1/getToken",
      "/wahlrecht/v1/doc/**",
      "/wahlrecht/v1/api-docs/**",
      "/error");

  private static final List<PathConfig> PATHS = buildPathConfigs();


  @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
  private String jwkSetUri;

  @Value("#{environment.CLIENT_SECRET}")
  private String clientSecret;


  @Bean
  public SecurityFilterChain securityFilterChain(@NonNull HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .authorizeHttpRequests()
          .requestMatchers(PERMITTED_PATHS.toArray(new String[0]))
          .permitAll()
          .and()
        .authorizeHttpRequests()
          .anyRequest()
          .authenticated()
          .and()
        .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
        .addFilterAfter(createPolicyEnforcerFilter(), BearerTokenAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
  }

  private ServletPolicyEnforcerFilter createPolicyEnforcerFilter() {
    return new ServletPolicyEnforcerFilter(new ConfigurationResolver() {
      @Override
      public PolicyEnforcerConfig resolve(HttpRequest request) {
        try {
          PolicyEnforcerConfig policyEnforcerConfig = JsonSerialization.readValue(
              getClass().getResourceAsStream("/policy-enforcer.json"), PolicyEnforcerConfig.class);
          policyEnforcerConfig.setCredentials(Map.of("secret", clientSecret));
          policyEnforcerConfig.setPaths(PATHS);

          return policyEnforcerConfig;
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    });
  }

  @NonNull
  private static List<PathConfig> buildPathConfigs() {
    List<PathConfig> paths = new ArrayList<>();
    for (String path : PERMITTED_PATHS) {
      PathConfig pathConfig = new PathConfig();
      pathConfig.setPath(path.replace("**", "*"));
      pathConfig.setEnforcementMode(EnforcementMode.DISABLED);
      paths.add(pathConfig);
    }
    return paths;
  }

}

