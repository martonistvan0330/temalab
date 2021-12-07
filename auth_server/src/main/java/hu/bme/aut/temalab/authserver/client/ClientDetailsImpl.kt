package hu.bme.aut.temalab.authserver.client

import hu.bme.aut.temalab.authserver.user.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.stream.Collectors

class ClientDetailsImpl(private val client: Client) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority?> {
        return client.roles!!.stream().map { role: String? ->
            SimpleGrantedAuthority(
                role
            )
        }.collect(Collectors.toList())
    }

    override fun getPassword(): String {
        return client.secret!!
    }

    override fun getUsername(): String {
        return client.name!!
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return client.isEnabled
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
