package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import jakarta.validation.Valid
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlAgendaItemDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlAgendaItemMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedAgendaItemRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class OparlAgendaItemController(
    private val urlGenerator: OparlUrlGenerator,
    private val agendaItemMapper: OparlAgendaItemMapper,
    private val listPaginationMapper: OparlListPaginationMapper,
    private val listLinksMapper: OparlListLinksMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedAgendaItemRepository: ResolvedAgendaItemRepository,
) {
    @GetMapping("oparl/v1.1/body/{bodyId}/agendaitem")
    fun list(
        @PathVariable bodyId: UUID,
        @ParameterObject @Valid pagination: OparlPaginationRequestDto,
    ): ResponseEntity<OparlListDto<OparlAgendaItemDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val agendaItems = resolvedAgendaItemRepository
            .findAllByBody_IdOrderById(bodyId, pagination.toPageable())
            .map(agendaItemMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.agendaItemList(bodyId, pagination.page),
                data = agendaItems.toList(),
                pagination = listPaginationMapper.toDto(agendaItems),
                links = listLinksMapper.toDto(
                    agendaItems,
                    { page -> urlGenerator.agendaItemList(bodyId, page) }
                ),
            )
        )
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/agendaitem/{agendaItemId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable agendaItemId: UUID,
    ): ResponseEntity<OparlAgendaItemDto> {
        val agendaItem = resolvedAgendaItemRepository.findByIdAndBody_Id(agendaItemId, bodyId)

        if (!agendaItem.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(agendaItemMapper.toDto(agendaItem.get()))
    }
}
