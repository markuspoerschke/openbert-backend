package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlLegislativeTermDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlLegislativeTermMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedLegislativeTermRepository
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class OparlLegislativeTermController(
    private val urlGenerator: OparlUrlGenerator,
    private val legislativeTermMapper: OparlLegislativeTermMapper,
    private val listPaginationMapper: OparlListPaginationMapper,
    private val listLinksMapper: OparlListLinksMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedLegislativeTermRepository: ResolvedLegislativeTermRepository,
) {
    @GetMapping("oparl/v1.1/body/{bodyId}/legislativeterm")
    fun list(
        @PathVariable bodyId: UUID,
        @RequestParam page: Int = 1,
    ): ResponseEntity<OparlListDto<OparlLegislativeTermDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val legislativeTerms = resolvedLegislativeTermRepository
            // please note that we need to subtract 1 from the page number because oparl expects a 1-based page number
            .findAllByBody_IdOrderById(bodyId, PageRequest.of(page - 1, PAGE_SIZE))
            .map(legislativeTermMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.legislativeTermList(bodyId, page),
                data = legislativeTerms.toList(),
                pagination = listPaginationMapper.toDto(legislativeTerms),
                links = listLinksMapper.toDto(
                    legislativeTerms,
                    { page -> urlGenerator.legislativeTermList(bodyId, page) }
                ),
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

    companion object {
        private const val PAGE_SIZE = 25
    }
}
