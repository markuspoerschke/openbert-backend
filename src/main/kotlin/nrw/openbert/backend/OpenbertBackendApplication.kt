package nrw.openbert.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OpenbertBackendApplication

fun main(args: Array<String>) {
    runApplication<OpenbertBackendApplication>(*args)
}
