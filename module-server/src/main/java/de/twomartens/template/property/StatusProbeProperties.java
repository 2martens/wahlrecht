package de.twomartens.template.property;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "de.twomartens.template.statusprobe")
@Schema(description = "Properties, to configure this Application")
public class StatusProbeProperties {

  @DurationUnit(ChronoUnit.SECONDS)
  private Duration scheduleDuration;

  @DurationUnit(ChronoUnit.MINUTES)
  private Duration maxKafkaFailureDuration;

  private int maxBlobFailureCount;

  private int maxFailurePercent;
}
