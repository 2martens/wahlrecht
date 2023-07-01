package de.twomartens.template.monitoring.actuator;

import de.twomartens.template.monitoring.statusprobe.StatusProbe;
import java.time.Clock;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.HealthIndicator;

@Slf4j
public abstract class AbstractStatusProbeHealthIndicator extends AbstractHealthIndicator
    implements HealthIndicator {

  public static final String MESSAGE_KEY = "message";
  public static final String LAST_STATUS_CHANGE_KEY = "lastStatusChange";

  private final StatusProbe statusProbe;

  public AbstractStatusProbeHealthIndicator(Clock timeProvider, Preparable headerInterceptor,
      StatusProbe statusProbe) {
    super(timeProvider, headerInterceptor);
    this.statusProbe = statusProbe;
  }

  @Override
  protected Health determineHealth() {
    Builder healthBuilder = Health.status(statusProbe.getStatus());
    Optional.ofNullable(statusProbe.getLastStatusChange())
        .ifPresent(l -> healthBuilder.withDetail(LAST_STATUS_CHANGE_KEY, l));
    Optional.ofNullable(statusProbe.getThrowable()).ifPresent(healthBuilder::withException);
    Optional.ofNullable(statusProbe.getMessage())
        .ifPresent(m -> healthBuilder.withDetail(MESSAGE_KEY, m));
    return healthBuilder.build();
  }

}
