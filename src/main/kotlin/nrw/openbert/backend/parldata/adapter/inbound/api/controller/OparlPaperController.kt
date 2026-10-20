package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaperDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlPaperMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedPaperRepository
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
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
        @RequestParam page: Int = 1,
    ): ResponseEntity<OparlListDto<OparlPaperDto>> {
        if (page < 1) {
            return ResponseEntity.badRequest().build()
        }

        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val papers = resolvedPaperRepository
            // please note that we need to subtract 1 from the page number because oparl expects a 1-based page number
            .findAllByBody_IdOrderById(bodyId, PageRequest.of(page - 1, PAGE_SIZE))
            .map(paperMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.paperList(bodyId, page),
                data = papers.toList(),
                pagination = listPaginationMapper.toDto(papers),
                links = listLinksMapper.toDto(
                    papers,
                    { page -> urlGenerator.paperList(bodyId, page) }
                ),
            )
        )
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/paper/{paperId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable paperId: UUID,
    ): ResponseEntity<OparlPaperDto> {
        val paper = resolvedPaperRepository.findByIdAndBody_Id(paperId, bodyId)

        if (!paper.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(paperMapper.toDto(paper.get()))
    }

    companion object {
        private const val PAGE_SIZE = 25
    }
}
