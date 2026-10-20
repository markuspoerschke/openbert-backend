package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlLocationDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlLocationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedLocationRepository
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

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
        @RequestParam page: Int = 1,
    ): ResponseEntity<OparlListDto<OparlLocationDto>> {
        if (page < 1) {
            return ResponseEntity.badRequest().build()
        }

        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val locations = resolvedLocationRepository
            // please note that we need to subtract 1 from the page number because oparl expects a 1-based page number
            .findAllByBody_IdOrderById(bodyId, PageRequest.of(page - 1, PAGE_SIZE))
            .map(locationMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.locationList(bodyId, page),
                data = locations.toList(),
                pagination = listPaginationMapper.toDto(locations),
                links = listLinksMapper.toDto(
                    locations,
                    { page -> urlGenerator.locationList(bodyId, page) }
                ),
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

    companion object {
        private const val PAGE_SIZE = 25
    }
}
