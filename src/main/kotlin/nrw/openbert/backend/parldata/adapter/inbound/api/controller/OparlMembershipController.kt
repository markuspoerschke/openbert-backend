package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlMembershipDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import nrw.openbert.backend.parldata.adapter.inbound.api.error.BadRequestProblem
import nrw.openbert.backend.parldata.adapter.inbound.api.error.NotFoundProblem
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlMembershipMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.specification.toListSpecification
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedMembershipRepository
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Tag(
    name = "Oparl Membership",
    externalDocs =
        ExternalDocumentation(
            "Oparl Specs",
            "https://oparl.org/spezifikation/online-ansicht/#entity-membership",
        ),
)
@RestController
class OparlMembershipController(
    private val urlGenerator: OparlUrlGenerator,
    private val membershipMapper: OparlMembershipMapper,
    private val listPaginationMapper: OparlListPaginationMapper,
    private val listLinksMapper: OparlListLinksMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedMembershipRepository: ResolvedMembershipRepository,
) {
    @BadRequestProblem
    @NotFoundProblem
    @GetMapping("oparl/v1.1/body/{bodyId}/membership")
    fun list(
        @PathVariable bodyId: UUID,
        @ParameterObject @Valid pagination: OparlPaginationRequestDto,
    ): ResponseEntity<OparlListDto<OparlMembershipDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Body $bodyId does not exist.")
        }

        val memberships =
            resolvedMembershipRepository
                .findAll(pagination.toListSpecification(bodyId = bodyId), pagination.toSpringPageRequest())
                .map { membershipMapper.toDto(it) }

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.membership.list(bodyId, pagination),
                data = memberships.toList(),
                pagination = listPaginationMapper.toDto(memberships),
                links =
                    listLinksMapper.toDto(
                        memberships,
                        { page -> urlGenerator.membership.list(bodyId, pagination.copy(page = page)) },
                    ),
            ),
        )
    }

    @NotFoundProblem
    @GetMapping("oparl/v1.1/body/{bodyId}/membership/{membershipId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable membershipId: UUID,
    ): ResponseEntity<OparlMembershipDto> {
        val membership =
            resolvedMembershipRepository
                .findByIdAndBodyId(membershipId, bodyId)
                .orElseThrow {
                    ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Membership $membershipId does not exist in body $bodyId.",
                    )
                }

        return ResponseEntity.ok(membershipMapper.toDto(membership))
    }
}
