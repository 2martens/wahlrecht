package de.twomartens.wahlrecht.controller

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.RestTemplate

@Controller
@RequestMapping(value = ["/wahlrecht"])
class ThirdPartyController(@Qualifier("restTemplate") private val template: RestTemplate) {
    @GetMapping(path = ["/thirdParty"])
    fun send(): ResponseEntity<String> {
        return template.getForEntity("https://www.google.de", String::class.java)
    }
}