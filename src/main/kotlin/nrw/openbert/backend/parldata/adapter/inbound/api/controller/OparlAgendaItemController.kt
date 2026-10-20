package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlAgendaItemDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlAgendaItemMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedAgendaItemRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
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
        @RequestParam page: Int = 1,
    ): ResponseEntity<OparlListDto<OparlAgendaItemDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val agendaItems = resolvedAgendaItemRepository
            // please note that we need to subtract 1 from the page number because oparl expects a 1-based page number
            .findAllByBody_IdOrderById(bodyId, PageRequest.of(page - 1, PAGE_SIZE))
            .map(agendaItemMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.agendaItemList(bodyId, page),
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

    companion object {
        private const val PAGE_SIZE = 25
    }
}
