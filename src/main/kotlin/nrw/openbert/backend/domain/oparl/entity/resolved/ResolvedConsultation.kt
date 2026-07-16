package nrw.openbert.backend.domain.oparl.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractConsultation
import nrw.openbert.backend.domain.oparl.entity.raw.RawConsultation
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId

@Entity(name = "oparl_resolved_consultation")
class ResolvedConsultation(id: OparlEntityId) : AbstractConsultation(id) {
    companion object {
        fun fromRaw(raw: RawConsultation): ResolvedConsultation {
            return ResolvedConsultation(raw.id).apply { applyValuesFrom(raw) }
        }
    }
}
