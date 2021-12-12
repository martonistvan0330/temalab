package hu.bme.aut.temalab.resourceserver

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MyController {
    @GetMapping("/")
    fun index(@AuthenticationPrincipal jwt: Jwt?): String {
        return String.format("Hello, %s!", if (jwt == null) "guest" else jwt.subject ?: "guest")
    }

    @GetMapping("/message")
    fun message(): String {
        return "secret message"
    }
}