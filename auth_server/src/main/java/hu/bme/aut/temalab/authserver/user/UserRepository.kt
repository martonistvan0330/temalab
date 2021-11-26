package hu.bme.aut.temalab.authserver.user

import hu.bme.aut.temalab.authserver.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User?, Long?> {
    fun existsByUsername(username: String?): Boolean
    fun findByUsername(username: String?): Optional<User>
}