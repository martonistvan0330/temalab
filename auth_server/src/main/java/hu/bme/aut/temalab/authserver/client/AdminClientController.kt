package hu.bme.aut.temalab.authserver.client

import net.minidev.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/oauth/admin/clients")
class AdminClientController {

    @Autowired
    var clientRepository: ClientRepository? = null

    @Autowired
    private val clientPasswordEncoder: PasswordEncoder? = null

    private lateinit var response: JSONObject

    @GetMapping("/all")
    @Secured
    fun getAll(): List<Client?> {
        return clientRepository!!.findAll()
    }

    @GetMapping("{id}")
    @Secured
    fun get(@PathVariable id: Long): ResponseEntity<Client?> {
        if (!clientRepository!!.existsById(id)) {
            return ResponseEntity.notFound().build()
        } else {
            val client = clientRepository!!.findById(id).get()
            return ResponseEntity.ok(client)
        }
    }

    @PutMapping("disable/{id}")
    @Secured
    fun disable(@PathVariable id: Long): ResponseEntity<Client?> {
        if (!clientRepository!!.existsById(id)) {
            return ResponseEntity.notFound().build()
        } else {
            val client = clientRepository!!.findById(id).get()
            client.isEnabled = false
            clientRepository!!.save(client)
            return ResponseEntity.ok(client)
        }
    }

    @PutMapping("enable/{id}")
    @Secured
    fun enable(@PathVariable id: Long): ResponseEntity<Client?> {
        if (!clientRepository!!.existsById(id)) {
            return ResponseEntity.notFound().build()
        } else {
            val client = clientRepository!!.findById(id).get()
            client.isEnabled = true
            clientRepository!!.save(client)
            return ResponseEntity.ok(client)
        }
    }
}