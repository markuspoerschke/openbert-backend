package nrw.openbert.backend.infrastructure.oparl.api.controller.v1

import nrw.openbert.backend.infrastructure.oparl.api.OparlUrlGenerator
import nrw.openbert.backend.infrastructure.oparl.api.dto.v1.OparlSystemDto
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class OparlSystemController(
    private val urlGenerator: OparlUrlGenerator,
) {
    @GetMapping("oparl/1.1/system")
    fun get(): ResponseEntity<OparlSystemDto> {
        val baseUrl = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .build()
            .toUriString()

        val responseDto = OparlSystemDto(
            "$baseUrl/oparl/1.1/system",
            "https://schema.oparl.org/1.1/System",
            "https://schema.oparl.org/1.1/",
            urlGenerator.bodiesUrl(),
            name = "Openbert"
        )

        return ResponseEntity.ok(responseDto)
    }
}