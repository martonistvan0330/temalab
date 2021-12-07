package hu.bme.aut.temalab.authserver

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
internal class JwkSetEndpoint {
    @Autowired
    private val rsaJWKAccess: RSAKey? = null

    @get:ResponseBody
    @get:GetMapping("/.well-known/jwks.json")
    val key: Map<String, Any>
        get() {
            val rsaPublicJWK = rsaJWKAccess!!.toPublicJWK()
            return JWKSet(rsaPublicJWK).toJSONObject()
        }
}