package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import jakarta.validation.Valid
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlMembershipDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlMembershipMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedMembershipRepository
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class OparlMembershipController(
    private val urlGenerator: OparlUrlGenerator,
    private val membershipMapper: OparlMembershipMapper,
    private val listPaginationMapper: OparlListPaginationMapper,
    private val listLinksMapper: OparlListLinksMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedMembershipRepository: ResolvedMembershipRepository,
) {
    @GetMapping("oparl/v1.1/body/{bodyId}/membership")
    fun list(
        @PathVariable bodyId: UUID,
        @ParameterObject @Valid pagination: OparlPaginationRequestDto,
    ): ResponseEntity<OparlListDto<OparlMembershipDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val memberships = resolvedMembershipRepository
            .findAllByBodyIdOrderById(bodyId, pagination.toPageable())
            .map { membershipMapper.toDto(it) }

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.membership.list(bodyId, pagination.page),
                data = memberships.toList(),
                pagination = listPaginationMapper.toDto(memberships),
                links = listLinksMapper.toDto(
                    memberships,
                    { page -> urlGenerator.membership.list(bodyId, page) }
                ),
            )
        )
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/membership/{membershipId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable membershipId: UUID,
    ): ResponseEntity<OparlMembershipDto> {
        val membership = resolvedMembershipRepository.findByIdAndBodyId(membershipId, bodyId)

        if (!membership.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(membershipMapper.toDto(membership.get()))
    }
}
