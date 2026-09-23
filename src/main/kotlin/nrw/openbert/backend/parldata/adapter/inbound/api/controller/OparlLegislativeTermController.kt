package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlLegislativeTermDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlLegislativeTermMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedLegislativeTermRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class OparlLegislativeTermController(
    private val urlGenerator: OparlUrlGenerator,
    private val legislativeTermMapper: OparlLegislativeTermMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedLegislativeTermRepository: ResolvedLegislativeTermRepository,
) {
    @GetMapping("oparl/v1.1/body/{bodyId}/legislativeterm")
    fun list(@PathVariable bodyId: UUID): ResponseEntity<OparlListDto<OparlLegislativeTermDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val legislativeTerms = resolvedLegislativeTermRepository
            .findAllByBody_IdOrderById(bodyId)
            .map(legislativeTermMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.legislativeTermList(bodyId),
                data = legislativeTerms,
            )
        )
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/legislativeterm/{legislativeTermId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable legislativeTermId: UUID,
    ): ResponseEntity<OparlLegislativeTermDto> {
        val legislativeTerm = resolvedLegislativeTermRepository
            .findByIdAndBody_Id(legislativeTermId, bodyId)

        if (!legislativeTerm.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(legislativeTermMapper.toDto(legislativeTerm.get()))
    }
}
