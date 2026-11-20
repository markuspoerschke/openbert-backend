package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlAgendaItemDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import nrw.openbert.backend.parldata.adapter.inbound.api.error.BadRequestProblem
import nrw.openbert.backend.parldata.adapter.inbound.api.error.NotFoundProblem
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlAgendaItemMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedAgendaItemRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Tag(
    name = "Oparl Agenda Item",
    externalDocs =
        ExternalDocumentation(
            "Oparl Specs",
            "https://oparl.org/spezifikation/online-ansicht/#entity-agendaitem",
        ),
)
@RestController
class OparlAgendaItemController(
    private val urlGenerator: OparlUrlGenerator,
    private val agendaItemMapper: OparlAgendaItemMapper,
    private val listPaginationMapper: OparlListPaginationMapper,
    private val listLinksMapper: OparlListLinksMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedAgendaItemRepository: ResolvedAgendaItemRepository,
) {
    @BadRequestProblem
    @NotFoundProblem
    @GetMapping("oparl/v1.1/body/{bodyId}/agendaitem")
    fun list(
        @PathVariable bodyId: UUID,
        @ParameterObject @Valid pagination: OparlPaginationRequestDto,
    ): ResponseEntity<OparlListDto<OparlAgendaItemDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Body $bodyId does not exist.")
        }

        val agendaItems =
            resolvedAgendaItemRepository
                .findAllByBodyIdOrderById(bodyId, pagination.toSpringPageRequest())
                .map(agendaItemMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.agendaItem.list(bodyId, pagination.page, pagination.size),
                data = agendaItems.toList(),
                pagination = listPaginationMapper.toDto(agendaItems),
                links =
                    listLinksMapper.toDto(
                        agendaItems,
                        { page -> urlGenerator.agendaItem.list(bodyId, page, pagination.size) },
                    ),
            ),
        )
    }

    @NotFoundProblem
    @GetMapping("oparl/v1.1/body/{bodyId}/agendaitem/{agendaItemId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable agendaItemId: UUID,
    ): ResponseEntity<OparlAgendaItemDto> {
        val agendaItem =
            resolvedAgendaItemRepository
                .findByIdAndBodyId(agendaItemId, bodyId)
                .orElseThrow {
                    ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Agenda item $agendaItemId does not exist in body $bodyId.",
                    )
                }

        return ResponseEntity.ok(agendaItemMapper.toDto(agendaItem))
    }
}
