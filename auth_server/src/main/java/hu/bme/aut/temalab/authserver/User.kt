package hu.bme.aut.temalab.authserver

import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id

@Entity
class User {
    @Id
    var name: String? = null

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password: String? = null
    var isEnabled = false

    @ElementCollection(fetch = FetchType.EAGER)
    var roles: List<String>? = null

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (name == null) 0 else name!!.hashCode()
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as User
        if (name == null) {
            if (other.name != null) return false
        } else if (name != other.name) return false
        return true
    }
}