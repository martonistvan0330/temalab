package hu.bme.aut.temalab.resourceserver

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class ResourceServerApplication

fun main(args: Array<String>) {
    SpringApplication.run(ResourceServerApplication::class.java, *args)
}