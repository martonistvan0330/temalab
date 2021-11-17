package hu.bme.aut.temalab.authserver.user

import hu.bme.aut.temalab.authserver.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User?, String?>