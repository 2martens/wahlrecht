package de.twomartens.wahlrecht.property

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import java.time.ZoneId

@RefreshScope
@ConfigurationProperties(prefix = "de.twomartens.wahlrecht")
@Schema(description = "Properties, to configure this Application")
data class ServiceProperties(var defaultTimeZone: ZoneId, var greeting: String)
