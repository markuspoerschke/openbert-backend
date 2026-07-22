package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.oparl.domain.entity.AbstractLegislativeTerm
import nrw.openbert.backend.oparl.domain.entity.raw.RawLegislativeTerm
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_resolved_legislativeTerm")
class ResolvedLegislativeTerm(systemId: UUID, url: OparlUrl) : AbstractLegislativeTerm(systemId, url) {
    companion object {
        fun fromRaw(raw: RawLegislativeTerm): ResolvedLegislativeTerm {
            return ResolvedLegislativeTerm(raw.systemId, raw.url).apply { applyValuesFrom(raw) }
        }
    }
}
