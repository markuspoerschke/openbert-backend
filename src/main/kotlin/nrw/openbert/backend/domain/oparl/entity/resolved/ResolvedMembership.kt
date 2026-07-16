package nrw.openbert.backend.domain.oparl.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractMembership
import nrw.openbert.backend.domain.oparl.entity.raw.RawMembership
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId

@Entity(name = "oparl_resolved_membership")
class ResolvedMembership(id: OparlEntityId) : AbstractMembership(id) {
    companion object {
        fun fromRaw(raw: RawMembership): ResolvedMembership {
            return ResolvedMembership(raw.id).apply { applyValuesFrom(raw) }
        }
    }
}
