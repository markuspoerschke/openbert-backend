package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaperDto
import nrw.openbert.backend.parldata.adapter.inbound.api.error.BadRequestProblem
import nrw.openbert.backend.parldata.adapter.inbound.api.error.NotFoundProblem
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlPaperMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.specification.toListSpecification
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedPaperRepository
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Tag(
    name = "Oparl Paper",
    externalDocs =
        ExternalDocumentation(
            "Oparl Specs",
            "https://oparl.org/spezifikation/online-ansicht/#entity-paper",
        ),
)
@RestController
class OparlPaperController(
    private val urlGenerator: OparlUrlGenerator,
    private val paperMapper: OparlPaperMapper,
    private val listPaginationMapper: OparlListPaginationMapper,
    private val listLinksMapper: OparlListLinksMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedPaperRepository: ResolvedPaperRepository,
) {
    @BadRequestProblem
    @NotFoundProblem
    @GetMapping("oparl/v1.1/body/{bodyId}/paper")
    fun list(
        @PathVariable bodyId: UUID,
        @ParameterObject @Valid pagination: OparlPaginationRequestDto,
    ): ResponseEntity<OparlListDto<OparlPaperDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Body $bodyId does not exist.")
        }

        val papers =
            resolvedPaperRepository
                .findAll(pagination.toListSpecification(bodyId = bodyId), pagination.toSpringPageRequest())
                .map(paperMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.paper.list(bodyId, pagination),
                data = papers.toList(),
                pagination = listPaginationMapper.toDto(papers),
                links =
                    listLinksMapper.toDto(
                        papers,
                        { page -> urlGenerator.paper.list(bodyId, pagination.copy(page = page)) },
                    ),
            ),
        )
    }

    @NotFoundProblem
    @GetMapping("oparl/v1.1/body/{bodyId}/paper/{paperId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable paperId: UUID,
    ): ResponseEntity<OparlPaperDto> {
        val paper =
            resolvedPaperRepository
                .findByIdAndBodyId(paperId, bodyId)
                .orElseThrow {
                    ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Paper $paperId does not exist in body $bodyId.",
                    )
                }

        return ResponseEntity.ok(paperMapper.toDto(paper))
    }
}
