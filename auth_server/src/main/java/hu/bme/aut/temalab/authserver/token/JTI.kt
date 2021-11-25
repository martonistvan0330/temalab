package hu.bme.aut.temalab.authserver.token

import javax.persistence.Embeddable

@Embeddable
class JTI {
    var jti: String? = null
}