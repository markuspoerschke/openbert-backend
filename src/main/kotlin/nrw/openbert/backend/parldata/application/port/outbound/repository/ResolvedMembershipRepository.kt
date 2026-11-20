package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedMembership
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional
import java.util.UUID

interface ResolvedMembershipRepository :
    JpaRepository<ResolvedMembership, UUID>,
    JpaSpecificationExecutor<ResolvedMembership> {
    fun findByResource(resource: OparlResource): ResolvedMembership?

    fun findByIdAndBodyId(
        id: UUID,
        bodyId: UUID,
    ): Optional<ResolvedMembership>

    fun findAllByPersonIdAndBodyIdOrderById(
        personId: UUID,
        bodyId: UUID,
    ): List<ResolvedMembership>

    fun findAllByOrganizationIdAndBodyIdOrderById(
        organizationId: UUID,
        bodyId: UUID,
    ): List<ResolvedMembership>
}
