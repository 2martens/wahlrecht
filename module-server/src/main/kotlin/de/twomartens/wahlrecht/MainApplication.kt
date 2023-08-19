package de.twomartens.wahlrecht

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@EnableMongoAuditing
@EnableScheduling
@SpringBootApplication
@EnableReactiveMongoRepositories
open class MainApplication

fun main(args: Array<String>) {
    runApplication<MainApplication>(*args)
}