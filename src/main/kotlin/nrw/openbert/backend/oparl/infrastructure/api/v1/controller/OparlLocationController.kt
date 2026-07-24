package nrw.openbert.backend.oparl.infrastructure.api.v1.controller

import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlListDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlLocationDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.mapper.OparlLocationMapper
import nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedBodyRepository
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedLocationRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class OparlLocationController(
    private val urlGenerator: OparlUrlGenerator,
    private val locationMapper: OparlLocationMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedLocationRepository: ResolvedLocationRepository,
) {
    @GetMapping("oparl/v1.1/body/{bodyId}/location")
    fun list(@PathVariable bodyId: UUID): ResponseEntity<OparlListDto<OparlLocationDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val locations = resolvedLocationRepository
            .findAllByBody_IdOrderById(bodyId)
            .map(locationMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.locationList(bodyId),
                data = locations,
            )
        )
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/location/{locationId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable locationId: UUID,
    ): ResponseEntity<OparlLocationDto> {
        val location = resolvedLocationRepository.findByIdAndBody_Id(locationId, bodyId)

        if (!location.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(locationMapper.toDto(location.get()))
    }
}
