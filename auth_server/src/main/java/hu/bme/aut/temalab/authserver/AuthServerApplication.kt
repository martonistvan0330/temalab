package hu.bme.aut.temalab.authserver

import hu.bme.aut.temalab.authserver.client.Client
import hu.bme.aut.temalab.authserver.client.ClientRepository
import hu.bme.aut.temalab.authserver.user.User
import hu.bme.aut.temalab.authserver.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootApplication
open class AuthServerApplication : CommandLineRunner {
    @Autowired
    private val userRepository: UserRepository? = null

    @Autowired
    private val clientRepository: ClientRepository? = null

    @Autowired
    private val userPasswordEncoder: PasswordEncoder? = null

    @Autowired
    private val clientPasswordEncoder: PasswordEncoder? = null

    @Throws(Exception::class)
    override fun run(vararg args: String) {
        val user = User()
        user.name = "demo"
        user.password = userPasswordEncoder!!.encode("demo")
        user.isEnabled = true
        user.roles = listOf("ROLE_USER")

        val admin = User()
        admin.name = "admin"
        admin.password = userPasswordEncoder!!.encode("admin")
        admin.isEnabled = true
        admin.roles = listOf("ROLE_ADMIN")

        userRepository!!.saveAll(listOf(user, admin))

        val basicClient = Client()
        basicClient.username = "client"
        basicClient.password = clientPasswordEncoder!!.encode("secret")
        basicClient.isEnabled = true
        basicClient.roles = listOf("ROLE_CLIENT")

        val adminClient = Client()
        adminClient.username = "admin"
        adminClient.password = clientPasswordEncoder!!.encode("admin")
        adminClient.isEnabled = true
        adminClient.roles = listOf("ROLE_ADMIN")

        clientRepository!!.saveAll(listOf(basicClient, adminClient))
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(AuthServerApplication::class.java, *args)
}