package nrw.openbert.backend.oparl.infrastructure.api.v1.controller

import nrw.openbert.backend.oparl.domain.entity.AbstractLocation
import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedLocation
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlListDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlLocationDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.oparl.infrastructure.persitence.repository.ResolvedBodyRepository
import nrw.openbert.backend.oparl.infrastructure.persitence.repository.ResolvedLocationRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class OparlLocationController(
    private val urlGenerator: OparlUrlGenerator,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedLocationRepository: ResolvedLocationRepository,
) {
    @GetMapping("oparl/v1.1/body/{bodyId}/location")
    fun list(@PathVariable bodyId: UUID): ResponseEntity<OparlListDto<OparlLocationDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.locationList(bodyId),
                data = resolvedLocationRepository.findAllByBody_IdOrderById(bodyId).map { it.toDto() },
            )
        )
    }

    @GetMapping("oparl/v1.1/location/{id}")
    fun get(@PathVariable id: UUID): ResponseEntity<OparlLocationDto> {
        val location = resolvedLocationRepository.findById(id)

        if (!location.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(location.get().toDto())
    }

    private fun ResolvedLocation.toDto(): OparlLocationDto {
        return OparlLocationDto(
            id = urlGenerator.location(id),
            type = AbstractLocation.TYPE,
            description = description,
            streetAddress = streetAddress,
            room = room,
            postalCode = postalCode,
            subLocality = subLocality,
            locality = locality,
            bodies = listOf(urlGenerator.body(body.id)),
            keyword = keyword.toList().takeIf { it.isNotEmpty() },
            created = created.toString(),
            modified = modified.toString(),
            deleted = deleted,
        )
    }
}
