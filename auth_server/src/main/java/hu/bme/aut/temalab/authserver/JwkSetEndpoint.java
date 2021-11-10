package hu.bme.aut.temalab.authserver;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

@Controller
class JwkSetEndpoint {
    @Autowired
    private RSAKey rsaJWK;

    @GetMapping("/.well-known/jwks.json")
    @ResponseBody
    public Map<String, Object> getKey() {
        RSAKey rsaPublicJWK = rsaJWK.toPublicJWK();
        return new JWKSet(rsaPublicJWK).toJSONObject();
    }
}