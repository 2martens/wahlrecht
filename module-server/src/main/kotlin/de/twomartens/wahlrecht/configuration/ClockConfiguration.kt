package de.twomartens.wahlrecht.configuration

import de.twomartens.wahlrecht.property.ServiceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
open class ClockConfiguration(val serviceProperties: ServiceProperties) {

    @Bean
    open fun clock(): Clock {
        return Clock.system(serviceProperties.defaultTimeZone)
    }
}