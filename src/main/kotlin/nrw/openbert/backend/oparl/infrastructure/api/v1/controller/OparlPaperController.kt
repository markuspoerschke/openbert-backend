package nrw.openbert.backend.oparl.infrastructure.api.v1.controller

import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlListDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlPaperDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.mapper.OparlPaperMapper
import nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedBodyRepository
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedPaperRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class OparlPaperController(
    private val urlGenerator: OparlUrlGenerator,
    private val paperMapper: OparlPaperMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedPaperRepository: ResolvedPaperRepository,
) {
    @GetMapping("oparl/v1.1/body/{bodyId}/paper")
    fun list(@PathVariable bodyId: UUID): ResponseEntity<OparlListDto<OparlPaperDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val papers = resolvedPaperRepository
            .findAllByBody_IdOrderById(bodyId)
            .map(paperMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.paperList(bodyId),
                data = papers,
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
}
