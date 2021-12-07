package hu.bme.aut.temalab.authserver.user

import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.*

@Entity
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var username: String? = null

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password: String? = null

    var isEnabled = false

    @ElementCollection(fetch = FetchType.EAGER)
    var roles: List<String>? = null

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (username == null) 0 else username!!.hashCode()
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as User
        if (username == null) {
            if (other.username != null) return false
        } else if (username != other.username) return false
        return true
    }
}