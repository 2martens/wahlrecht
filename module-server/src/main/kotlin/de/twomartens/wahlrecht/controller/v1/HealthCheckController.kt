package de.twomartens.wahlrecht.controller.v1

import io.swagger.v3.oas.annotations.Hidden
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/wahlrecht/v1"])
class HealthCheckController {

    @Hidden
    @GetMapping("/healthCheck")
    fun healthCheck(message: String): String {
        return message
    }
}