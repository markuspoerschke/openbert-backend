package nrw.openbert.backend.oparl.infrastructure.persitence.repository

import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedOrganization
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ResolvedOrganizationRepository : JpaRepository<ResolvedOrganization, UUID> {
    fun findAllByBody_IdOrderById(bodyId: UUID): List<ResolvedOrganization>
}
