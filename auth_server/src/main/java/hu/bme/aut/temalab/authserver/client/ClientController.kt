package hu.bme.aut.temalab.authserver.client

import hu.bme.aut.temalab.authserver.user.User
import hu.bme.aut.temalab.authserver.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/clients")
class ClientController {
    @Autowired
    var clientRepository: ClientRepository? = null

    @get:GetMapping
    val all: List<Client?>
        get() = clientRepository!!.findAll()
}