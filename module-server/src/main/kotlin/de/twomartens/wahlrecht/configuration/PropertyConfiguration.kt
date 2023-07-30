package de.twomartens.wahlrecht.configuration

import de.twomartens.wahlrecht.property.RestTemplateTimeoutProperties
import de.twomartens.wahlrecht.property.ServiceProperties
import de.twomartens.wahlrecht.property.StatusProbeProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(RestTemplateTimeoutProperties::class, ServiceProperties::class,
    StatusProbeProperties::class)
open class PropertyConfiguration