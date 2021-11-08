package hu.bme.aut.temalab.authserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
public class AuthServerApplication implements CommandLineRunner {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        User user = new User();
        user.setName("demo");
        user.setPassword(passwordEncoder.encode("demo"));
        user.setEnabled(true);
        user.setRoles(List.of("ROLE_USER"));

        User admin = new User();
        admin.setName("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setEnabled(true);
        admin.setRoles(List.of("ROLE_ADMIN"));

        repository.saveAll(List.of(user, admin));
    }
}
