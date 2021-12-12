package hu.bme.aut.temalab.authserver

import com.nimbusds.jose.*
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import hu.bme.aut.temalab.authserver.user.UserRepository
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
    private val rsaJWKAccess: RSAKey? = null

    @Autowired
    private val rsaJWKRefresh: RSAKey? = null

    private lateinit var response: JSONObject

    @PostMapping
    @Secured
    @Throws(JOSEException::class, ParseException::class)
    fun createToken(@RequestBody urlParameters: String): ResponseEntity<JSONObject> {
        values = bodyHandler.parseParameters(urlParameters)
        response = JSONObject()
        if (!containsAllKeys()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        } else if (getErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        } else {
            return ResponseEntity.ok(generateResponse())
        }
    }

    private fun containsAllKeys() : Boolean {
        var contains = true
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

    private fun getErrors() : Boolean {
        var error = false
        if (values["grant_type"] == "password" || values["grant_type"] == "refresh_token") {
            if (values["grant_type"] == "password") {
                if (userRepository!!.existsByUsername(values["username"])) {
                    val user = userRepository!!.findByUsername(values["username"]).get()
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
                if (!valid(values["refresh_token"])) {
                    response["error_refresh_token"] = "invalid refresh token"
                    error = true
                }
            }
        } else {
            response["error_grant_type"] = "invalid grant_type"
            error = true
        }
        return error
    }

    private fun valid(refreshToken: String?): Boolean {
        val signedJWT = SignedJWT.parse(refreshToken)
        val expirationTime = signedJWT.jwtClaimsSet.expirationTime.time
        val rsaPublicJWKRefresh = rsaJWKRefresh!!.toPublicJWK()
        val verifier = RSASSAVerifier(rsaPublicJWKRefresh)
        if (signedJWT.verify(verifier)) {
            return Date().time < expirationTime
        }
        return false
    }

    private fun getUsername(refreshToken: String?): String? {
        val signedJWT = SignedJWT.parse(refreshToken)
        return signedJWT.jwtClaimsSet.subject
    }

    private fun generateResponse(): JSONObject {
        var username: String? = null
        if (values["grant_type"] == "password") {
            username = values["username"]
        }
        if (values["grant_type"] == "refresh_token") {
            username = getUsername(values["refresh_token"])
        }
        val jti = generateAccessToken(username)
        response["token_type"] = "bearer"
        generateRefreshToken(username, jti)
        response["scope"] = "sample"
        response["jti"] = jti
        return response
    }

    private fun generateAccessToken(username: String?): String{
        val signer: JWSSigner = RSASSASigner(rsaJWKAccess)
        val jti = UUID.randomUUID().toString()
        val claimsSet = JWTClaimsSet.Builder()
            .subject(username)
            .expirationTime(Date(Date().time + 600 * 1000))
            .jwtID(jti)
            .build()
        val signedJWT = SignedJWT(
            JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWKAccess!!.keyID).build(),
            claimsSet
        )
        signedJWT.sign(signer)
        val s = signedJWT.serialize()
        response["access_token"] = s
        val secondsLeft = (signedJWT.jwtClaimsSet.expirationTime.time - Date().time) / 1000
        response["access_token_expires_in"] = secondsLeft
        return jti
    }

    private fun generateRefreshToken(username: String?, jti: String){
        val signer: JWSSigner = RSASSASigner(rsaJWKRefresh)
        val claimsSet = JWTClaimsSet.Builder()
            .subject(username)
            .expirationTime(Date(Date().time + 3600 * 1000))
            .jwtID(jti)
            .build()
        val signedJWT = SignedJWT(
            JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWKRefresh!!.keyID).build(),
            claimsSet
        )
        signedJWT.sign(signer)
        val s = signedJWT.serialize()
        response["refresh_token"] = s
        val secondsLeft = (signedJWT.jwtClaimsSet.expirationTime.time - Date().time) / 1000
        response["refresh_token_expires_in"] = secondsLeft
    }
}
