package nrw.openbert.backend.oparl.infrastructure.persistence.repository

import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedLegislativeTerm
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ResolvedLegislativeTermRepository : JpaRepository<ResolvedLegislativeTerm, UUID> {
    fun findByResource(resource: OparlResource): ResolvedLegislativeTerm?

    fun findAllByBody_IdOrderById(bodyId: UUID): List<ResolvedLegislativeTerm>

    fun findByIdAndBody_Id(id: UUID, bodyId: UUID): Optional<ResolvedLegislativeTerm>
}
