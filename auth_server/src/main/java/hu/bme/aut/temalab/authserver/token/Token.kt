package hu.bme.aut.temalab.authserver.token

import hu.bme.aut.temalab.authserver.client.Client
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import javax.persistence.*

@Entity
class Token {
    @Id
    @GeneratedValue
    var id: Long? = null

    var username: String? = null

    @Column(unique=true)
    var jti: String? = null

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (id == null) 0 else id!!.hashCode()
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as Client
        if (username == null) {
            if (other.username != null) return false
        } else if (username != other.username) return false
        return true
    }

}