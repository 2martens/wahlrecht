package de.twomartens.wahlrecht.configuration

import de.twomartens.wahlrecht.security.SpringPolicyEnforcerFilter
import org.keycloak.adapters.authorization.spi.ConfigurationResolver
import org.keycloak.adapters.authorization.spi.HttpRequest
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig.EnforcementMode
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig.PathConfig
import org.keycloak.util.JsonSerialization
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain
import java.io.IOException

@Configuration
@EnableWebSecurity
open class WebSecurityConfiguration {
    @Value("\${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private lateinit var jwkSetUri: String

    @Value("#{environment.CLIENT_SECRET}")
    private lateinit var clientSecret: String

    @Bean
    @Throws(Exception::class)
    open fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { it.requestMatchers(*PERMITTED_PATHS.toTypedArray<String>()).permitAll() }
            .authorizeHttpRequests { it.requestMatchers(HttpMethod.OPTIONS).permitAll() }
            .authorizeHttpRequests { it.anyRequest().authenticated() }
        return http.build()
    }

    @Bean
    open fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build()
    }

    private fun createPolicyEnforcerFilter(): SpringPolicyEnforcerFilter {
        return SpringPolicyEnforcerFilter(object : ConfigurationResolver {
            override fun resolve(request: HttpRequest): PolicyEnforcerConfig {
                return try {
                    val policyEnforcerConfig = JsonSerialization.readValue(
                        javaClass.getResourceAsStream("/policy-enforcer.json"), PolicyEnforcerConfig::class.java
                    )
                    policyEnforcerConfig.credentials = mapOf(Pair("secret", clientSecret))
                    if (request.method == HttpMethod.OPTIONS.name()) {
                        // always allow options request
                        policyEnforcerConfig.enforcementMode = EnforcementMode.DISABLED
                    } else {
                        policyEnforcerConfig.paths = PATHS
                    }
                    policyEnforcerConfig
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            }
        })
    }

    companion object {
        private val PERMITTED_PATHS: Collection<String> = listOf(
            "/wahlrecht/v1/healthCheck",
            "/actuator/**",
            "/wahlrecht/v1/doc/**",
            "/wahlrecht/v1/api-docs/**",
            "/error",
            "/wahlrecht/version",
            "/wahlrecht/thirdParty",
        )
        private val PATHS = buildPathConfigs()

        private fun buildPathConfigs(): List<PathConfig> {
            val paths: MutableList<PathConfig> = mutableListOf()
            for (path in PERMITTED_PATHS) {
                val pathConfig = PathConfig()
                pathConfig.path = path.replace("**", "*")
                pathConfig.enforcementMode = EnforcementMode.DISABLED
                paths.add(pathConfig)
            }
            return paths
        }
    }
}
