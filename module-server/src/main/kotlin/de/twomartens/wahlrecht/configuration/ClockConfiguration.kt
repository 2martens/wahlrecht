package de.twomartens.wahlrecht.configuration

import de.twomartens.wahlrecht.property.ServiceProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
open class ClockConfiguration(private val serviceProperties: ServiceProperties) {

    @Bean
    @RefreshScope
    open fun clock(): Clock {
        return Clock.system(serviceProperties.defaultTimeZone)
    }
}