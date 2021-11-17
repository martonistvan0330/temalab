package hu.bme.aut.temalab.authserver.client

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class ClientDetailsServiceImpl : UserDetailsService {
    @Autowired
    private val clientRepository: ClientRepository? = null

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val client = clientRepository!!.findByUsername(username)
        return if (!client.isPresent) throw UsernameNotFoundException("$username is an invalid username") else ClientDetailsImpl(
            client.get()
        )
    }

    @Bean
    fun clientPasswordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}