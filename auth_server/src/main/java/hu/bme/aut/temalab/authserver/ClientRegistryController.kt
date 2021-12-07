package hu.bme.aut.temalab.authserver

import com.nimbusds.jose.JOSEException
import hu.bme.aut.temalab.authserver.client.Client
import hu.bme.aut.temalab.authserver.client.ClientRepository
import hu.bme.aut.temalab.authserver.user.User
import net.minidev.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.text.ParseException
import java.util.HashMap

@RestController
@RequestMapping("/oauth/clients")
class ClientRegistryController {
    @Autowired
    var clientRepository: ClientRepository? = null

    @Autowired
    private val clientPasswordEncoder: PasswordEncoder? = null

    private val bodyHandler: BodyHandler = BodyHandler()

    private lateinit var values: MutableMap<String, String>

    private lateinit var response: JSONObject

    @PostMapping("/add")
    @Throws(JOSEException::class, ParseException::class)
    fun addClient(@RequestBody urlParameters: String): ResponseEntity<JSONObject> {
        values = bodyHandler.parseParameters(urlParameters)
        response = JSONObject()
        if (!containsAllKeys()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        } else if (!usernameAvailable()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        } else {
            addClientToRepository()
            return ResponseEntity.ok(response)
        }
    }

    private fun containsAllKeys() : Boolean {
        if (values.containsKey("username") && values.containsKey("password")) {
            return true
        }
        if (!values.containsKey("username")) {
            response["error_username"] = "no username"
        }
        if (!values.containsKey("password")) {
            response["error_password"] = "no password"
        }
        return false
    }

    private fun usernameAvailable(): Boolean {
        if (clientRepository!!.existsByUsername(values["username"])) {
            response["error_username"] = "username not available"
            return false
        } else {
            return true
        }
    }

    private fun addClientToRepository() {
        var client = Client()
        client.username = values["username"]
        client.password = clientPasswordEncoder!!.encode(values["password"])
        client.isEnabled = true;
        client.roles = listOf("ROLE_CLIENT");
        clientRepository!!.saveAll(listOf(client))
        response["message"] = "client saved"
        response["username"] = values["username"]
    }
}