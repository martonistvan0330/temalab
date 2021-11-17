package hu.bme.aut.temalab.authserver

import hu.bme.aut.temalab.authserver.user.UserDetailsImpl
import hu.bme.aut.temalab.authserver.user.UserRepository
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
class UserDetailsServiceImpl : UserDetailsService {
    @Autowired
    private val userRepository: UserRepository? = null

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository!!.findById(username)
        return if (!user.isPresent) throw UsernameNotFoundException("$username is an invalid username") else UserDetailsImpl(
            user.get()
        )
    }

    @Bean
    fun userPasswordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}