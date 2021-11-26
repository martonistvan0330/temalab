package hu.bme.aut.temalab.authserver.token

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TokenRepository : JpaRepository<Token?, Long?> {
    fun existsByJti(jti: String?): Boolean
    fun findByJti(jti: String?): Optional<Token>
}