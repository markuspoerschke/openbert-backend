package nrw.openbert.backend.oparl.domain.repository.raw

import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawLegislativeTerm
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RawLegislativeTermRepository : JpaRepository<RawLegislativeTerm, UUID> {
    fun findByResource(resource: OparlResource): RawLegislativeTerm?
}
