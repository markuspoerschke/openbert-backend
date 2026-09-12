package nrw.openbert.backend.oparl.infrastructure.api.v1.controller

import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlBodyDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlListDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.mapper.OparlBodyMapper
import nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedBodyRepository
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
