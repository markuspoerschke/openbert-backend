package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import jakarta.validation.Valid
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaperDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlPaperMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedPaperRepository
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class OparlPaperController(
    private val urlGenerator: OparlUrlGenerator,
    private val paperMapper: OparlPaperMapper,
    private val listPaginationMapper: OparlListPaginationMapper,
    private val listLinksMapper: OparlListLinksMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedPaperRepository: ResolvedPaperRepository,
) {
    @GetMapping("oparl/v1.1/body/{bodyId}/paper")
    fun list(
        @PathVariable bodyId: UUID,
        @ParameterObject @Valid pagination: OparlPaginationRequestDto,
    ): ResponseEntity<OparlListDto<OparlPaperDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val papers = resolvedPaperRepository
            .findAllByBodyIdOrderById(bodyId, pagination.toPageable())
            .map(paperMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.paper.list(bodyId, pagination.page),
                data = papers.toList(),
                pagination = listPaginationMapper.toDto(papers),
                links = listLinksMapper.toDto(
                    papers,
                    { page -> urlGenerator.paper.list(bodyId, page) }
                ),
            )
        )
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/paper/{paperId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable paperId: UUID,
    ): ResponseEntity<OparlPaperDto> {
        val paper = resolvedPaperRepository.findByIdAndBodyId(paperId, bodyId)

        if (!paper.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(paperMapper.toDto(paper.get()))
    }
}
