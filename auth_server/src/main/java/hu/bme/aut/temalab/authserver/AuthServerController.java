package hu.bme.aut.temalab.authserver;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/oauth/token")
public class AuthServerController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private KeyPair keyPair;

    @PostMapping
    public JSONObject createToken(@RequestBody String urlParameters) throws JOSEException {
        String[] parameters = urlParameters.split("&");
        Map<String, String> values = new HashMap<String, String>();
        String key;
        String value;
        for (String parameter : parameters) {
            key = parameter.split("=")[0];
            value = parameter.split("=")[1];
            values.put(key, value);
        }
        JSONObject response = new JSONObject();
        if (!values.containsKey("username")) {
            response.put("error", "no username");
            return response;
        }
        if (!values.containsKey("password")) {
            response.put("error", "no password");
            return response;
        }
        if (!values.containsKey("grant_type") ) {
            response.put("error", "no grant_type");
            return response;
        } else if (!values.get("grant_type").equals("password")) {
            response.put("error", "invalid grant_type");
            return response;
        }
        User user = null;
        if (userRepository.existsById(values.get("username"))) {
            user = userRepository.findById(values.get("username")).get();
        } else {
            response.put("error", "user not found");
            return response;
        }
        if(passwordEncoder.matches(values.get("password"), user.getPassword())) {
            response.put("username", values.get("username"));
            response.put("password", values.get("password"));
            response.put("repo_username", user.getName());
            response.put("repo_password", user.getPassword());
            response.put("encoded_password", passwordEncoder.encode(values.get("password")));
            RSAKey rsaJWK = new RSAKeyGenerator(2048)
                    .keyID("123")
                    .generate();
            RSAKey rsaPublicJWK = rsaJWK.toPublicJWK();
            JWSSigner signer = new RSASSASigner(rsaJWK);
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(values.get("username"))
                    .issuer("https://c2id.com")
                    .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                    .build();
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
                    claimsSet);
            signedJWT.sign(signer);
            String s = signedJWT.serialize();
            response.put("access_token", s);
            response.put("token_type", "bearer");
            response.put("refresh_token", "asdasdasd");
            response.put("expires_in", 599);
            response.put("scope", "sample");
            response.put("jti", "asdasdasdasd123");
        } else {
            response.put("error", "wrong password");
        }
        return response;
    }
}
