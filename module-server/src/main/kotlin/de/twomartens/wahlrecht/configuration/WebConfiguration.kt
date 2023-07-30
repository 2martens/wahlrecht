package de.twomartens.wahlrecht.configuration;

import de.twomartens.wahlrecht.interceptor.HeaderInterceptorRest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

  private final HeaderInterceptorRest headerInterceptorRest;

  @Override
  public void addInterceptors(@NonNull InterceptorRegistry registry) {
    registry.addInterceptor(headerInterceptorRest);
  }

  @Override
  public void addCorsMappings(@NonNull CorsRegistry registry) {
    CorsRegistration registration = registry.addMapping("/**");
    registration.allowedMethods(HttpMethod.GET.name(), HttpMethod.POST.name(),
        HttpMethod.PUT.name(), HttpMethod.OPTIONS.name());
    registration.allowCredentials(true);
    registration.allowedOrigins("http://localhost:4200");
  }

}
