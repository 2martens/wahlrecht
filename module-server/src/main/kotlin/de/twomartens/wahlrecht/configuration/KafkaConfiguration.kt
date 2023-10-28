package de.twomartens.wahlrecht.configuration

import org.apache.kafka.clients.admin.AdminClientConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin

@Configuration
open class KafkaConfiguration {
  @Value(value = "\${spring.kafka.bootstrap-servers[0]}")
  private val bootstrapServers: List<String> = mutableListOf()

  @Bean
  open fun kafkaAdmin(): KafkaAdmin {
    val configs: MutableMap<String, Any?> = HashMap()
    configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers.first()
    return KafkaAdmin(configs)
  }
}