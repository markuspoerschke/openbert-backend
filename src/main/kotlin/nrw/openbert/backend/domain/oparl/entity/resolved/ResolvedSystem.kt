package nrw.openbert.backend.domain.oparl.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractSystem
import nrw.openbert.backend.domain.oparl.entity.raw.RawSystem
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId

@Entity(name = "oparl_resolved_system")
class ResolvedSystem(id: OparlEntityId) : AbstractSystem(id) {
    companion object {
        fun fromRaw(raw: RawSystem): ResolvedSystem {
            return ResolvedSystem(raw.id).apply { applyValuesFrom(raw) }
        }
    }
}
