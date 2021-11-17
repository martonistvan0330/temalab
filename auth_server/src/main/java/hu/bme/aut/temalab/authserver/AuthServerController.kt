package hu.bme.aut.temalab.authserver

import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import hu.bme.aut.temalab.authserver.client.Client
import hu.bme.aut.temalab.authserver.user.User
import hu.bme.aut.temalab.authserver.user.UserRepository
import net.minidev.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.text.ParseException
import java.util.*

@RestController
@RequestMapping("/oauth/token")
class AuthServerController {
    @Autowired
    var userRepository: UserRepository? = null

    @Autowired
    private val userPasswordEncoder: PasswordEncoder? = null

    private val bodyHandler: BodyHandler = BodyHandler()

    private lateinit var values: MutableMap<String, String>

    @Autowired
    private val rsaJWK: RSAKey? = null

    @PostMapping
    @Throws(JOSEException::class, ParseException::class)
    fun createToken(@RequestBody urlParameters: String): JSONObject {
        values = bodyHandler.parseParameters(urlParameters)
        val response = JSONObject()
        if (!containsAllKeys(response)) {
            return response;
        } else if (getErrors(response)) {
            return response
        } else {
            return generateToken()
        }
    }

    private fun containsAllKeys(response: JSONObject) : Boolean {
        if (values.containsKey("username") && values.containsKey("password") && values.containsKey("grant_type")) {
            return true
        }
        if (!values.containsKey("username")) {
            response["error_username"] = "no username"
        }
        if (!values.containsKey("password")) {
            response["error_password"] = "no password"
        }
        if (!values.containsKey("grant_type")) {
            response["error_grant_type"] = "no grant_type"
        }
        return false
    }

    private fun getErrors(response: JSONObject) : Boolean {
        if (values["grant_type"] == "password" || values["grant_type"] == "refresh_token") {
            if (userRepository!!.existsById(values["username"])) {
                val user = userRepository!!.findById(values["username"]).get();
                if (userPasswordEncoder!!.matches(values["password"], user.password)) {
                    return false
                } else {
                    response["error_password"] = "wrong password"
                    return true
                }
            } else {
                response["error_username"] = "user not found"
                return true
            }
        } else {
            response["error_grant_type"] = "invalid grant_type"
            return true
        }
    }

    private fun generateToken(): JSONObject{
        val response = JSONObject()
        val user = userRepository!!.findById(values["username"]).get()
        val rsaPublicJWK = rsaJWK!!.toPublicJWK()
        val signer: JWSSigner = RSASSASigner(rsaJWK)
        val claimsSet = JWTClaimsSet.Builder()
            .subject(values["username"])
            .expirationTime(Date(Date().time + 600 * 1000))
            .build()
        val signedJWT = SignedJWT(
            JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.keyID).build(),
            claimsSet
        )
        signedJWT.sign(signer)
        val s = signedJWT.serialize()
        response["access_token"] = s
        response["token_type"] = "bearer"
        val secondsLeft = (signedJWT.jwtClaimsSet.expirationTime.time - Date().time) / 1000
        response["expires_in"] = secondsLeft
        return response
    }
}
