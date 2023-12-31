package de.twomartens.wahlrecht.configuration

import de.twomartens.wahlrecht.interceptor.HeaderInterceptorRest
import de.twomartens.wahlrecht.interceptor.LoggingInterceptorRest
import de.twomartens.wahlrecht.property.RestTemplateTimeoutProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
open class RestTemplateConfiguration {
    @Bean
    open fun restTemplate(
        headerInterceptorRest: HeaderInterceptorRest,
        loggingInterceptor: LoggingInterceptorRest,
        restTemplateTimeoutProperties: RestTemplateTimeoutProperties
    ): RestTemplate {
        return RestTemplateBuilder()
            .additionalInterceptors(headerInterceptorRest, loggingInterceptor)
            .setConnectTimeout(restTemplateTimeoutProperties.connectionRestTemplateTimeoutInMillis)
            .setReadTimeout(restTemplateTimeoutProperties.readTimeoutRestTemplateInMillis)
            .build()
    }

    @Bean
    open fun restTemplateRestHealthIndicator(
        headerInterceptorRest: HeaderInterceptorRest,
        restTemplateTimeoutProperties: RestTemplateTimeoutProperties
    ): RestTemplate {
        return RestTemplateBuilder()
            .additionalInterceptors(headerInterceptorRest)
            .setConnectTimeout(restTemplateTimeoutProperties.connectionRestHealthIndicatorTimeoutInMillis)
            .setReadTimeout(restTemplateTimeoutProperties.readTimeoutRestHealthIndicatorInMillis)
            .build()
    }
}