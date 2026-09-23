package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlBodyDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlBodyMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class OparlBodyController(
    private val urlGenerator: OparlUrlGenerator,
    private val bodyMapper: OparlBodyMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
) {
    @GetMapping("oparl/v1.1/body")
    fun list(): ResponseEntity<OparlListDto<OparlBodyDto>> {
        val bodies = resolvedBodyRepository.findAllByOrderById().map(bodyMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                urlGenerator.bodyList(),
                bodies,
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
}
