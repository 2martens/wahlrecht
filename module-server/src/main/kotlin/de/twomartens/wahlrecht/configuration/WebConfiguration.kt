package de.twomartens.wahlrecht.configuration

import de.twomartens.wahlrecht.interceptor.HeaderInterceptorRest
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
open class WebConfiguration(private val headerInterceptorRest: HeaderInterceptorRest) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(headerInterceptorRest)
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        val registration = registry.addMapping("/**")
        registration.allowedMethods(
            HttpMethod.GET.name(), HttpMethod.POST.name(),
            HttpMethod.PUT.name(), HttpMethod.OPTIONS.name()
        )
        registration.allowCredentials(true)
        registration.allowedOrigins("http://localhost:4200")
    }
}
