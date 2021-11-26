package hu.bme.aut.temalab.authserver.user

import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.*

@Entity
@Table(name="users")
class User {
    @Id
    @Column(name="username")
    var username: String? = null

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name="password")
    var password: String? = null

    @Column(name="enabled")
    var isEnabled = false

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name="roles")
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