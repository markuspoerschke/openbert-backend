package nrw.openbert.backend

import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
    fromApplication<OpenbertBackendApplication>().with(TestcontainersConfiguration::class).run(*args)
}
