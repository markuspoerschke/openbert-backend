package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedOrganization
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional
import java.util.UUID

interface ResolvedOrganizationRepository :
    JpaRepository<ResolvedOrganization, UUID>,
    JpaSpecificationExecutor<ResolvedOrganization> {
    fun findByResource(resource: OparlResource): ResolvedOrganization?

    fun findByIdAndBodyId(
        id: UUID,
        bodyId: UUID,
    ): Optional<ResolvedOrganization>
}
