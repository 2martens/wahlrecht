package de.twomartens.wahlrecht.configuration

import de.twomartens.wahlrecht.monitoring.statusprobe.CountBasedStatusProbe
import de.twomartens.wahlrecht.monitoring.statusprobe.StatusProbe
import de.twomartens.wahlrecht.monitoring.statusprobe.StatusProbeCriticality
import de.twomartens.wahlrecht.monitoring.statusprobe.StatusProbeLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
open class StatusProbeConfiguration(private val clock: Clock) {
    @Bean
    open fun statusProbeLogger(): StatusProbeLogger {
        return StatusProbeLogger(clock)
    }

    @Bean
    open fun testStatusProbe(statusProbeLogger: StatusProbeLogger): StatusProbe {
        return CountBasedStatusProbe(
            1,
            clock, StatusProbeCriticality.K1, "testStatusProbe", statusProbeLogger
        )
    }
}