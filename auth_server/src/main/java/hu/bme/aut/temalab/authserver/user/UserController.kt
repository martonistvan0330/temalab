package hu.bme.aut.temalab.authserver

import hu.bme.aut.temalab.authserver.user.User
import hu.bme.aut.temalab.authserver.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController {
    @Autowired
    var userRepository: UserRepository? = null

    @get:GetMapping
    val all: List<User?>
        @Secured
        get() = userRepository!!.findAll()
}