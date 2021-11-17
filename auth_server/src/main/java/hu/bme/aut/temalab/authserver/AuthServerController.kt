package hu.bme.aut.temalab.authserver

import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
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
    private val passwordEncoder: PasswordEncoder? = null

    @Autowired
    private val rsaJWK: RSAKey? = null
    @PostMapping
    @Throws(JOSEException::class, ParseException::class)
    fun createToken(@RequestBody urlParameters: String): JSONObject {
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
        if (!values.containsKey("grant_type")) {
            response["error"] = "no grant_type"
            return response
        } else if (values["grant_type"] != "password") {
            response["error"] = "invalid grant_type"
            return response
        }
        var user: User? = null
        if (userRepository!!.existsById(values["username"])) {
            user = userRepository!!.findById(values["username"]).get()
        } else {
            response["error"] = "user not found"
            return response
        }
        if (passwordEncoder!!.matches(values["password"], user.password)) {
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
            response["scope"] = "sample"
        } else {
            response["error"] = "wrong password"
        }
        return response
    }
}
