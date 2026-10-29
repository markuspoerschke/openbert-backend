package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlLegislativeTermDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlLegislativeTermMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedLegislativeTermRepository
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Tag(
    name = "Oparl Legislative Term",
    externalDocs = ExternalDocumentation(
        "Oparl Specs",
        "https://oparl.org/spezifikation/online-ansicht/#entity-legislativeterm",
    ),
)
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
        @ParameterObject @Valid pagination: OparlPaginationRequestDto,
    ): ResponseEntity<OparlListDto<OparlLegislativeTermDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val legislativeTerms = resolvedLegislativeTermRepository
            .findAllByBodyIdOrderById(bodyId, pagination.toPageable())
            .map(legislativeTermMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.legislativeTerm.list(bodyId, pagination.page),
                data = legislativeTerms.toList(),
                pagination = listPaginationMapper.toDto(legislativeTerms),
                links = listLinksMapper.toDto(
                    legislativeTerms,
                    { page -> urlGenerator.legislativeTerm.list(bodyId, page) }
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
            .findByIdAndBodyId(legislativeTermId, bodyId)

        if (!legislativeTerm.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(legislativeTermMapper.toDto(legislativeTerm.get()))
    }
}
