package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.tags.Tag
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlSystemDto
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Tag(
    name = "Oparl System",
    externalDocs = ExternalDocumentation(
        "Oparl Specs",
        "https://oparl.org/spezifikation/online-ansicht/#entity-system",
    ),
)
@RestController
class OparlSystemController(
    private val urlGenerator: OparlUrlGenerator,
) {
    @GetMapping("oparl/v1.1/system")
    fun get(): ResponseEntity<OparlSystemDto> {
        val baseUrl = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .build()
            .toUriString()

        val responseDto = OparlSystemDto(
            "$baseUrl/oparl/v1.1/system",
            "https://schema.oparl.org/1.1/System",
            "https://schema.oparl.org/1.1/",
            urlGenerator.bodyList(),
            name = "Openbert"
        )

        return ResponseEntity.ok(responseDto)
    }
}
