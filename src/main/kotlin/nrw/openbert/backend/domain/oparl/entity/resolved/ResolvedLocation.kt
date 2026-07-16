package nrw.openbert.backend.domain.oparl.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractLocation
import nrw.openbert.backend.domain.oparl.entity.raw.RawLocation
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId

@Entity(name = "oparl_resolved_location")
class ResolvedLocation(id: OparlEntityId) : AbstractLocation(id) {
    companion object {
        fun fromRaw(raw: RawLocation): ResolvedLocation {
            return ResolvedLocation(raw.id).apply { applyValuesFrom(raw) }
        }
    }
}
