package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlBodyDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlBodyMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class OparlBodyController(
    private val urlGenerator: OparlUrlGenerator,
    private val bodyMapper: OparlBodyMapper,
    private val listPaginationMapper: OparlListPaginationMapper,
    private val listLinksMapper: OparlListLinksMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
) {
    @GetMapping("oparl/v1.1/body")
    fun list(@RequestParam page: Int = 1): ResponseEntity<OparlListDto<OparlBodyDto>> {
        val bodies = resolvedBodyRepository
            // please note that we need to subtract 1 from the page number because oparl expects a 1-based page number
            .findAllByOrderById(PageRequest.of(page - 1, PAGE_SIZE))
            .map(bodyMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.bodyList(page),
                data = bodies.toList(),
                pagination = listPaginationMapper.toDto(bodies),
                links = listLinksMapper.toDto(
                    bodies,
                    { page -> urlGenerator.bodyList(page) }
                ),
            )
        )
    }

    @GetMapping("oparl/v1.1/body/{id}")
    fun get(@PathVariable id: UUID): ResponseEntity<OparlBodyDto> {
        val resolvedBody = resolvedBodyRepository.findById(id)

        if (!resolvedBody.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(bodyMapper.toDto(resolvedBody.get()))
    }

    companion object {
        private const val PAGE_SIZE = 25
    }
}
