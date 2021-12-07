package hu.bme.aut.temalab.authserver.client

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ClientRepository : JpaRepository<Client?, Long?> {
    fun findByName(username: String?): Optional<Client>
    fun existsByName(username: String?): Boolean
}