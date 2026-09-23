package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawLegislativeTerm
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RawLegislativeTermRepository : JpaRepository<RawLegislativeTerm, UUID> {
    fun findByResource(resource: OparlResource): RawLegislativeTerm?
}
