package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPersonDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlPersonMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedPersonRepository
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Tag(
    name = "Oparl Person",
    externalDocs =
        ExternalDocumentation(
            "Oparl Specs",
            "https://oparl.org/spezifikation/online-ansicht/#entity-person",
        ),
)
@RestController
class OparlPersonController(
    private val urlGenerator: OparlUrlGenerator,
    private val personMapper: OparlPersonMapper,
    private val listPaginationMapper: OparlListPaginationMapper,
    private val listLinksMapper: OparlListLinksMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedPersonRepository: ResolvedPersonRepository,
) {
    @GetMapping("oparl/v1.1/body/{bodyId}/person")
    fun list(
        @PathVariable bodyId: UUID,
        @ParameterObject @Valid pagination: OparlPaginationRequestDto,
    ): ResponseEntity<OparlListDto<OparlPersonDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val persons =
            resolvedPersonRepository
                .findAllByBodyIdOrderById(bodyId, pagination.toSpringPageRequest())
                .map(personMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.person.list(bodyId, pagination.page),
                data = persons.toList(),
                pagination = listPaginationMapper.toDto(persons),
                links =
                    listLinksMapper.toDto(
                        persons,
                        { page -> urlGenerator.person.list(bodyId, page) },
                    ),
            ),
        )
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/person/{personId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable personId: UUID,
    ): ResponseEntity<OparlPersonDto> {
        val person = resolvedPersonRepository.findByIdAndBodyId(personId, bodyId)

        if (!person.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(personMapper.toDto(person.get()))
    }
}
