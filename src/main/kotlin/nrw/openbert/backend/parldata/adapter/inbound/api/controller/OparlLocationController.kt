package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlLocationDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlLocationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedLocationRepository
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Tag(
    name = "Oparl Location",
    externalDocs = ExternalDocumentation(
        "Oparl Specs",
        "https://oparl.org/spezifikation/online-ansicht/#entity-location",
    ),
)
@RestController
class OparlLocationController(
    private val urlGenerator: OparlUrlGenerator,
    private val locationMapper: OparlLocationMapper,
    private val listPaginationMapper: OparlListPaginationMapper,
    private val listLinksMapper: OparlListLinksMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedLocationRepository: ResolvedLocationRepository,
) {
    @GetMapping("oparl/v1.1/body/{bodyId}/location")
    fun list(
        @PathVariable bodyId: UUID,
        @ParameterObject @Valid pagination: OparlPaginationRequestDto,
    ): ResponseEntity<OparlListDto<OparlLocationDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val locations = resolvedLocationRepository
            .findAllByBodyIdOrderById(bodyId, pagination.toPageable())
            .map(locationMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.location.list(bodyId, pagination.page),
                data = locations.toList(),
                pagination = listPaginationMapper.toDto(locations),
                links = listLinksMapper.toDto(
                    locations,
                    { page -> urlGenerator.location.list(bodyId, page) }
                ),
            )
        )
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/location/{locationId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable locationId: UUID,
    ): ResponseEntity<OparlLocationDto> {
        val location = resolvedLocationRepository.findByIdAndBodyId(locationId, bodyId)

        if (!location.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(locationMapper.toDto(location.get()))
    }
}
