package hu.bme.aut.temalab.authserver

import com.nimbusds.jose.*
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
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
            return generateResponse()
        }
    }

    private fun containsAllKeys(response: JSONObject) : Boolean {
        var contains = true;
        if (values.containsKey("grant_type")) {
            if (values["grant_type"] == "password") {
                if (!values.containsKey("username")) {
                    response["error_username"] = "no username"
                    contains = false
                }
                if (!values.containsKey("password")) {
                    response["error_password"] = "no password"
                    contains = false
                }
            }
            if (values["grant_type"] == "refresh_token") {
                if (!values.containsKey("refresh_token")) {
                    response["error_refresh_token"] = "no refresh_token"
                    contains = false
                }
            }
        } else {
            response["error_grant_type"] = "no grant_type"
            contains = false
        }
        return contains
    }

    private fun getErrors(response: JSONObject) : Boolean {
        var error = false;
        if (values["grant_type"] == "password" || values["grant_type"] == "refresh_token") {
            if (values["grant_type"] == "password") {
                if (userRepository!!.existsById(values["username"])) {
                    val user = userRepository!!.findById(values["username"]).get();
                    if (!userPasswordEncoder!!.matches(values["password"], user.password)) {
                        response["error_password"] = "wrong password"
                        error = true
                    }
                } else {
                    response["error_username"] = "user not found"
                    error = true
                }
            }
            if (values["grant_type"] == "refresh_token") {

            }
        } else {
            response["error_grant_type"] = "invalid grant_type"
            error = true
        }
        return error
    }

    private fun generateResponse(): JSONObject {
        val response = JSONObject()
        val jti = generateAccessToken(response)
        response["token_type"] = "bearer"
        generateRefreshToken(response, jti)
        response["scope"] = "sample"
        response["jti"] = jti
        return response
    }

    private fun generateAccessToken(response: JSONObject): String{
        val signer: JWSSigner = RSASSASigner(rsaJWK)
        val jti = UUID.randomUUID().toString()
        val claimsSet = JWTClaimsSet.Builder()
            .subject(values["username"])
            .expirationTime(Date(Date().time + 600 * 1000))
            .jwtID(jti)
            .build()
        val signedJWT = SignedJWT(
            JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK!!.keyID).build(),
            claimsSet
        )
        signedJWT.sign(signer)
        val s = signedJWT.serialize()
        response["access_token"] = s
        val secondsLeft = (signedJWT.jwtClaimsSet.expirationTime.time - Date().time) / 1000
        response["expires_in"] = secondsLeft
        return jti
    }

    private fun generateRefreshToken(response: JSONObject, jti: String){
        val signer: JWSSigner = RSASSASigner(rsaJWK)
        val jti = UUID.randomUUID().toString()
        val claimsSet = JWTClaimsSet.Builder()
            .expirationTime(Date(Date().time + 600 * 1000))
            .jwtID(jti)
            .build()
        val signedJWT = SignedJWT(
            JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK!!.keyID).build(),
            claimsSet
        )
        signedJWT.sign(signer)
        val s = signedJWT.serialize()
        response["refresh_token"] = s
    }
}
