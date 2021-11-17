package hu.bme.aut.temalab.authserver

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.List

@SpringBootApplication
open class AuthServerApplication : CommandLineRunner {
    @Autowired
    private val repository: UserRepository? = null

    @Autowired
    private val passwordEncoder: PasswordEncoder? = null

    @Throws(Exception::class)
    override fun run(vararg args: String) {
        val user = User()
        user.name = "demo"
        user.password = passwordEncoder!!.encode("demo")
        user.isEnabled = true
        user.roles = listOf("ROLE_USER")
        val admin = User()
        admin.name = "admin"
        admin.password = passwordEncoder.encode("admin")
        admin.isEnabled = true
        admin.roles = listOf("ROLE_ADMIN")
        repository!!.saveAll(listOf(user, admin))
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(AuthServerApplication::class.java, *args)
}