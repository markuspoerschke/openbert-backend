package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedOrganization
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ResolvedOrganizationRepository : JpaRepository<ResolvedOrganization, UUID> {
    fun findByResource(resource: OparlResource): ResolvedOrganization?

    fun findAllByBody_IdOrderById(bodyId: UUID, pageable: Pageable): Page<ResolvedOrganization>

    fun findByIdAndBody_Id(id: UUID, bodyId: UUID): Optional<ResolvedOrganization>
}
