package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPersonDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlPersonMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedPersonRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class OparlPersonController(
    private val urlGenerator: OparlUrlGenerator,
    private val personMapper: OparlPersonMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedPersonRepository: ResolvedPersonRepository,
) {
    @GetMapping("oparl/v1.1/body/{bodyId}/person")
    fun list(@PathVariable bodyId: UUID): ResponseEntity<OparlListDto<OparlPersonDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val persons = resolvedPersonRepository
            .findAllByBody_IdOrderById(bodyId)
            .map(personMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.personList(bodyId),
                data = persons,
            )
        )
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/person/{personId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable personId: UUID,
    ): ResponseEntity<OparlPersonDto> {
        val person = resolvedPersonRepository.findByIdAndBody_Id(personId, bodyId)

        if (!person.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(personMapper.toDto(person.get()))
    }
}
