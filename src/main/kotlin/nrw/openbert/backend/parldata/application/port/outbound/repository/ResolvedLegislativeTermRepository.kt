package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedLegislativeTerm
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ResolvedLegislativeTermRepository : JpaRepository<ResolvedLegislativeTerm, UUID> {
    fun findByResource(resource: OparlResource): ResolvedLegislativeTerm?

    fun findAllByBody_IdOrderById(bodyId: UUID, pageable: Pageable): Page<ResolvedLegislativeTerm>

    fun findByIdAndBody_Id(id: UUID, bodyId: UUID): Optional<ResolvedLegislativeTerm>
}
