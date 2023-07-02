package de.twomartens.wahlrecht.property;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZoneId;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "de.twomartens.wahlrecht")
@Schema(description = "Properties, to configure this Application")
public class ServiceProperties {

  private ZoneId defaultTimeZone;

  private String greeting;

}