package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlBodyDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlBodyMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Tag(
    name = "Oparl Body",
    externalDocs =
        ExternalDocumentation(
            "Oparl Specs",
            "https://oparl.org/spezifikation/online-ansicht/#entity-body",
        ),
)
@RestController
class OparlBodyController(
    private val urlGenerator: OparlUrlGenerator,
    private val bodyMapper: OparlBodyMapper,
    private val listPaginationMapper: OparlListPaginationMapper,
    private val listLinksMapper: OparlListLinksMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
) {
    @GetMapping("oparl/v1.1/body")
    fun list(
        @ParameterObject @Valid pagination: OparlPaginationRequestDto,
    ): ResponseEntity<OparlListDto<OparlBodyDto>> {
        val bodies =
            resolvedBodyRepository
                .findAllByOrderById(pagination.toSpringPageRequest())
                .map(bodyMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.bodyList(pagination.page),
                data = bodies.toList(),
                pagination = listPaginationMapper.toDto(bodies),
                links =
                    listLinksMapper.toDto(
                        bodies,
                        { page -> urlGenerator.bodyList(page) },
                    ),
            ),
        )
    }

    @GetMapping("oparl/v1.1/body/{id}")
    fun get(
        @PathVariable id: UUID,
    ): ResponseEntity<OparlBodyDto> {
        val resolvedBody = resolvedBodyRepository.findById(id)

        if (!resolvedBody.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(bodyMapper.toDto(resolvedBody.get()))
    }
}
