package hu.bme.aut.temalab.authserver

import com.nimbusds.jose.JOSEException
import hu.bme.aut.temalab.authserver.client.Client
import hu.bme.aut.temalab.authserver.client.ClientRepository
import hu.bme.aut.temalab.authserver.user.User
import net.minidev.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.text.ParseException
import java.util.HashMap

@RestController
@RequestMapping("/oauth/client/add")
class ClientRegistryController {
    @Autowired
    var clientRepository: ClientRepository? = null

    @Autowired
    private val clientPasswordEncoder: PasswordEncoder? = null

    @PostMapping
    //@Secured("ROLE_ADMIN")
    @Throws(JOSEException::class, ParseException::class)
    fun addClient(@RequestBody urlParameters: String): JSONObject {
        val parameters = urlParameters.split("&".toRegex()).toTypedArray()
        val values: MutableMap<String, String> = HashMap()
        var key: String
        var value: String
        for (parameter in parameters) {
            key = parameter.split("=".toRegex()).toTypedArray()[0]
            value = parameter.split("=".toRegex()).toTypedArray()[1]
            values[key] = value
        }
        val response = JSONObject()
        if (!values.containsKey("username")) {
            response["error"] = "no username"
            return response
        }
        if (!values.containsKey("password")) {
            response["error"] = "no password"
            return response
        }
        if (clientRepository!!.existsByUsername(values["username"])) {
            response["error"] = "username not available"
        } else {
            var client = Client()
            client.username = values["username"]
            client.password = clientPasswordEncoder!!.encode(values["password"])
            client.isEnabled = true;
            client.roles = listOf("ROLE_CLIENT");
            clientRepository!!.saveAll(listOf(client))
            response["message"] = "client saved"
            response["username"] = values["username"]
        }
        return response
    }
}