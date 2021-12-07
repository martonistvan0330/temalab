package hu.bme.aut.temalab.authserver.client

import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.*

@Entity
class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var name: String? = null

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var secret: String? = null

    var isEnabled = false

    @ElementCollection(fetch = FetchType.EAGER)
    var roles: List<String>? = null

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
        if (id == null) {
            if (other.id != null) return false
        } else if (id != other.id) return false
        return true
    }
}