package de.twomartens.wahlrecht.monitoring.actuator;

import de.twomartens.wahlrecht.interceptor.HeaderInterceptorRest;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * A Health check which checks if the rest services are working.
 * <p>
 * If you have a complex service, you should think about an easy greeting or echo service, which
 * only tests the
 * network/service stack and not the full application.
 * <p>
 * The health check will be called by kubernetes to check if the container/pod should be in load
 * balancing. It is possible
 * to have as much health checks as you like.
 * <p>
 * There should be a health check which is ok not before all data is loaded.
 */
@Slf4j
@Component
public class RestHealthIndicator extends AbstractHealthIndicator implements HealthIndicator {

  private static final String URL_PATH = "/wahlrecht/v1/healthCheck";
  private static final String GET_PARAMETER = "message=";

  private final SecureRandom randomizer = new SecureRandom();
  private final RestTemplate restTemplateRestHealthIndicator;
  private final String urlPrefix;

  public RestHealthIndicator(Clock clock, HeaderInterceptorRest interceptor,
      ServerProperties serverProperties, RestTemplate restTemplateRestHealthIndicator) {
    super(clock, interceptor::markAsHealthCheck);
    this.restTemplateRestHealthIndicator = restTemplateRestHealthIndicator;
    urlPrefix = HTTP_PREFIX + HOST + HOST_PORT_SEPERATOR + serverProperties.getPort()
        + URL_PATH + PARAMETER_SEPERATOR + GET_PARAMETER;
  }

  /**
   * main method that determines the health of the service
   */
  @Override
  protected Health determineHealth() {
    String random = Integer.toString(randomizer.nextInt(100000, 999999));
    String url = urlPrefix + "{random}";
    ResponseEntity<String> response = restTemplateRestHealthIndicator.getForEntity(url, String.class, random);
    Status status = Optional.ofNullable(response.getBody())
        .filter(random::equals)
        .map(m -> Status.UP)
        .orElse(Status.DOWN);
    return Health.status(status)
        .withDetail(DETAIL_ENDPOINT_KEY, url)
        .build();
  }

}
