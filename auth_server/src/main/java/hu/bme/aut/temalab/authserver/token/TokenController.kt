package hu.bme.aut.temalab.authserver.token

import hu.bme.aut.temalab.authserver.client.Client
import hu.bme.aut.temalab.authserver.client.ClientRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/tokens")
class TokenController {
    @Autowired
    var tokenRepository: TokenRepository? = null

    @get:GetMapping
    val all: List<Token?>
        get() = tokenRepository!!.findAll()
}