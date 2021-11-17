package hu.bme.aut.temalab.authserver.client

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ClientRepository : JpaRepository<Client?, Long?> {
    abstract fun findByUsername(username: String?): Optional<Client>
    abstract fun existsByUsername(username: String?): Boolean
}