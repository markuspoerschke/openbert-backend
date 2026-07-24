package nrw.openbert.backend.oparl.infrastructure.persistence.repository

import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedMembership
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ResolvedMembershipRepository : JpaRepository<ResolvedMembership, UUID> {
    fun findAllByBody_IdOrderById(bodyId: UUID): List<ResolvedMembership>

    fun findByIdAndBody_Id(id: UUID, bodyId: UUID): Optional<ResolvedMembership>

    fun findAllByPerson_IdAndBody_IdOrderById(
        personId: UUID,
        bodyId: UUID,
    ): List<ResolvedMembership>

    fun findAllByOrganization_IdAndBody_IdOrderById(
        organizationId: UUID,
        bodyId: UUID,
    ): List<ResolvedMembership>
}
