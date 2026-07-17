package nrw.openbert.backend.infrastructure.oparl.api.controller.v1

import nrw.openbert.backend.infrastructure.oparl.api.dto.v1.OparlSystemDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class OparlSystemController {
    @GetMapping("oparl/1.1/system")
    fun get(): ResponseEntity<OparlSystemDto> {
        val responseDto = OparlSystemDto(
            "https://api.openbert.nrw",
            "https://schema.oparl.org/1.1/System",
            "https://schema.oparl.org/1.1/",
            "https://api.openbert.nrw/oparl/1.1/bodies",
            name = "Openbert"
        )

        return ResponseEntity.ok(responseDto)
    }
}