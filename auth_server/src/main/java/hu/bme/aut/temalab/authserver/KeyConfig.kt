package hu.bme.aut.temalab.authserver

import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class KeyConfig {

    @Bean
    open fun rsaJWK(): RSAKey {
        return try {
            RSAKeyGenerator(2048)
                .keyID("123")
                .generate()
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }
}