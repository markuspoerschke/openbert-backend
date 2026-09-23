package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlMembershipDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlMembershipMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedMembershipRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class OparlMembershipController(
    private val urlGenerator: OparlUrlGenerator,
    private val membershipMapper: OparlMembershipMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedMembershipRepository: ResolvedMembershipRepository,
) {
    @GetMapping("oparl/v1.1/body/{bodyId}/membership")
    fun list(@PathVariable bodyId: UUID): ResponseEntity<OparlListDto<OparlMembershipDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val memberships = resolvedMembershipRepository
            .findAllByBody_IdOrderById(bodyId)
            .map { membershipMapper.toDto(it) }

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.membershipList(bodyId),
                data = memberships,
            )
        )
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/membership/{membershipId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable membershipId: UUID,
    ): ResponseEntity<OparlMembershipDto> {
        val membership = resolvedMembershipRepository.findByIdAndBody_Id(membershipId, bodyId)

        if (!membership.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(membershipMapper.toDto(membership.get()))
    }
}
