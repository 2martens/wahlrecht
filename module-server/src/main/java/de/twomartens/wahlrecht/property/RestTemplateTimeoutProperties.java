package de.twomartens.wahlrecht.property;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "restwahlrecht.timeout")
@Component
public class RestTemplateTimeoutProperties {

  @DurationUnit(ChronoUnit.MILLIS)
  private Duration readTimeoutRestHealthIndicatorInMillis;
  @DurationUnit(ChronoUnit.MILLIS)
  private Duration connectionRestHealthIndicatorTimeoutInMillis;
  @DurationUnit(ChronoUnit.MILLIS)
  private Duration readTimeoutRestTemplateInMillis;
  @DurationUnit(ChronoUnit.MILLIS)
  private Duration connectionRestTemplateTimeoutInMillis;
}

