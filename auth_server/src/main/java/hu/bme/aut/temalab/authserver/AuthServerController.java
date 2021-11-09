package hu.bme.aut.temalab.authserver;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/oauth/token")
public class AuthServerController {

    @Autowired
    UserRepository userRepository;

    @PostMapping
    public String createToken(){
        return "token";
    }
}
