package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedLegislativeTerm
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional
import java.util.UUID

interface ResolvedLegislativeTermRepository :
    JpaRepository<ResolvedLegislativeTerm, UUID>,
    JpaSpecificationExecutor<ResolvedLegislativeTerm> {
    fun findByResource(resource: OparlResource): ResolvedLegislativeTerm?

    fun findByIdAndBodyId(
        id: UUID,
        bodyId: UUID,
    ): Optional<ResolvedLegislativeTerm>
}
