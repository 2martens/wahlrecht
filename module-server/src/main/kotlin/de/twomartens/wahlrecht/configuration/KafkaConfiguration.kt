package de.twomartens.wahlrecht.configuration

import org.apache.kafka.clients.admin.AdminClientConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.core.KafkaAdmin

@Configuration
open class KafkaConfiguration {
    @Bean
    @Profile("prod")
    open fun kafkaProd() = KafkaAdmin(mapOf(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to "kafka:9092"))

    @Bean
    @Profile("dev")
    open fun kafkaDev() = KafkaAdmin(mapOf(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092"))
}
