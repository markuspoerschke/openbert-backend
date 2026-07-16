package nrw.openbert.backend.domain.oparl.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractPerson
import nrw.openbert.backend.domain.oparl.entity.raw.RawPerson
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId

@Entity(name = "oparl_resolved_person")
class ResolvedPerson(id: OparlEntityId) : AbstractPerson(id) {
    companion object {
        fun fromRaw(raw: RawPerson): ResolvedPerson {
            return ResolvedPerson(raw.id).apply { applyValuesFrom(raw) }
        }
    }
}
