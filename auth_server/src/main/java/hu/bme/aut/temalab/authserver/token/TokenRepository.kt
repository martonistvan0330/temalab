package hu.bme.aut.temalab.authserver.token

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TokenRepository : JpaRepository<Token?, String?> {
    fun findByJtis_jti(jti: String?): Optional<Token>
    fun existsByJtis_jti(jti: String?): Boolean
}