package hu.bme.aut.temalab.authserver.client

import com.nimbusds.jose.JOSEException
import net.minidev.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.status
import org.springframework.security.access.annotation.Secured
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.text.ParseException
import java.util.*

@RestController
@RequestMapping("/oauth/clients")
class ClientController {
    @Autowired
    var clientRepository: ClientRepository? = null

    @Autowired
    private val clientPasswordEncoder: PasswordEncoder? = null

    private lateinit var response: JSONObject

    @PostMapping("/add")
    @Throws(JOSEException::class, ParseException::class)
    fun addClient(@RequestBody client: Client?): ResponseEntity<JSONObject> {
        response = JSONObject()
        if (client == null) {
            return ResponseEntity.unprocessableEntity().build()
        } else if (!containsAllKeys(client)) {
            return status(HttpStatus.BAD_REQUEST).body(response)
        } else if (!usernameAvailable(client)) {
            return status(HttpStatus.BAD_REQUEST).body(response)
        } else {
            addClientToRepository(client)
            return ResponseEntity.ok(response)
        }
    }

    private fun containsAllKeys(client: Client?) : Boolean {
        if (client?.name != null && client?.secret != null) {
            return true
        }
        if (client?.name == null) {
            response["error_username"] = "no username"
        }
        if (client?.secret == null) {
            response["error_password"] = "no password"
        }
        return false
    }

    private fun usernameAvailable(client: Client?): Boolean {
        if (clientRepository!!.existsByName(client?.name)) {
            response["error_username"] = "username not available"
            return false
        } else {
            return true
        }
    }

    private fun addClientToRepository(client: Client?) {
        client?.id = null
        client?.secret = clientPasswordEncoder?.encode(client?.secret)
        client?.isEnabled = true
        client?.roles = listOf("ROLE_CLIENT")
        clientRepository!!.saveAll(listOf(client))
        response["message"] = "client saved"
        response["username"] = client?.name
    }

    private fun decode(encodedNameSecret: String) : List<String> {
        val decodedBytes = Base64.getDecoder().decode(encodedNameSecret.substring(6))
        val decodedNameSecret = String(decodedBytes)
        return decodedNameSecret.split(":")
    }

    @PutMapping("/update")
    @Secured
    fun update(@RequestHeader("authorization") nameSecret: String, @RequestBody client: Client?) : ResponseEntity<Client?> {
        if (client == null) {
            return ResponseEntity.unprocessableEntity().build()
        } else {
            val decodedNameSecret = decode(nameSecret)
            val name = decodedNameSecret[0]
            val secret = decodedNameSecret[1]
            val oldClient = clientRepository!!.findByName(name).get()
            if (client?.name != null && !clientRepository!!.existsByName(client?.name)) {
                oldClient.name = client?.name
            }
            if (client?.secret != null) {
                oldClient.secret = clientPasswordEncoder!!.encode(client?.secret)
            }
            clientRepository!!.save(oldClient)
            return ResponseEntity.ok(oldClient)
        }
    }

    @DeleteMapping("/delete")
    @Secured
    fun delete(@RequestHeader("authorization") nameSecret: String) : ResponseEntity<Any> {
        val decodedNameSecret = decode(nameSecret)
        val name = decodedNameSecret[0]
        val client = clientRepository!!.findByName(name).get()
        clientRepository!!.delete(client)
        return ResponseEntity.noContent().build()
    }
}