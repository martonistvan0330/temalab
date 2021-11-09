package hu.bme.aut.temalab.authserver;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping
    public JSONObject createToken(@RequestBody String urlParameters) {
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
        if (!values.containsKey("grant_type") || !values.get("grant_type").equals("password")) {
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
        } else {
            response.put("error", "wrong password");
        }
        return response;
    }
}
