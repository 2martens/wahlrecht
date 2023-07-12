package de.twomartens.wahlrecht.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

  public static final String ROLE_USER = "USER";

  @Bean
  public SecurityFilterChain securityFilterChain(@NonNull HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .authorizeHttpRequests()
            .requestMatchers(HttpMethod.PUT, "/wahlrecht/v1/**")
            .authenticated()
            .and()
        .authorizeHttpRequests()
            .requestMatchers("/wahlrecht/v1/**", "/wahlrecht/version",
                "/error")
            .permitAll()
            .and()
        .authorizeHttpRequests()
          .requestMatchers("/actuator/**")
          .permitAll()
          .and()
        .authorizeHttpRequests()
            .requestMatchers("/resources/**")
            .permitAll()
            .and()
        .httpBasic();
    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails user = User.withDefaultPasswordEncoder()
        .username("user")
        .password("password")
        .roles(ROLE_USER)
        .build();
    return new InMemoryUserDetailsManager(user);
  }
}

