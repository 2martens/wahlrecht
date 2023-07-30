package de.twomartens.wahlrecht.service

import mu.KotlinLogging
import org.apache.logging.log4j.message.StringMapMessage
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.context.refresh.ContextRefresher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

/**
 * automatically reloads application*.yaml. reload can also be triggered manually by doing a post
 * request on http://localhost:12001/actuator/refresh
 */
@Service
class PropertyReloadService(@Qualifier("configDataContextRefresher") private val contextRefresher: ContextRefresher) {
    @Scheduled(
        fixedDelay = REFRESH_SECONDS.toLong(),
        initialDelay = REFRESH_SECONDS.toLong(),
        timeUnit = TimeUnit.SECONDS
    )
    fun refresh() {
        val properties = contextRefresher.refresh()
        if (properties.isNotEmpty()) {
            log.info(
                StringMapMessage()
                    .with(PARAM_MESSAGE, "properties changed")
                    .with(PARAM_PROPERTIES, properties.joinToString("\n"))
                    .asString()
            )
        }
    }

    companion object {
        const val REFRESH_SECONDS = 60
        const val PARAM_MESSAGE = "message"
        const val PARAM_PROPERTIES = "labels.properties"

        private val log = KotlinLogging.logger {}
    }
}
