package nrw.openbert.backend.domain.oparl.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractLegislativeTerm
import nrw.openbert.backend.domain.oparl.entity.raw.RawLegislativeTerm
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId

@Entity(name = "oparl_resolved_legislativeTerm")
class ResolvedLegislativeTerm(id: OparlEntityId) : AbstractLegislativeTerm(id) {
    companion object {
        fun fromRaw(raw: RawLegislativeTerm): ResolvedLegislativeTerm {
            return ResolvedLegislativeTerm(raw.id).apply { applyValuesFrom(raw) }
        }
    }
}
