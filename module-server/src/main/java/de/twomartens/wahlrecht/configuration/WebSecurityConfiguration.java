package de.twomartens.wahlrecht.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // (1)
public class WebSecurityConfiguration {

  public static final String ROLE_USER = "USER";

  @Bean
  public SecurityFilterChain securityFilterChain(@NonNull HttpSecurity http) throws Exception {  // (2)
    http
        .authorizeHttpRequests()
          .requestMatchers("/wahlrecht/v1/**", "/wahlrecht/version")
          .permitAll()
          .and()
        .authorizeHttpRequests()
          .requestMatchers("/resources/**")
          .permitAll()
          .and()
        .authorizeHttpRequests()
          .requestMatchers("/wahlrecht/v1/election",
              "/wahlrecht/v1/party/**",
              "/wahlrecht/v1/nomination/**")
          .authenticated() // (3)
          .and()
        .httpBasic(); // (7)
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

