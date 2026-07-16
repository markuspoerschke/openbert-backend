package nrw.openbert.backend.domain.oparl.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractBody
import nrw.openbert.backend.domain.oparl.entity.raw.RawBody
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId

@Entity(name = "oparl_resolved_body")
class ResolvedBody(id: OparlEntityId) : AbstractBody(id) {
    companion object {
        fun fromRaw(raw: RawBody): ResolvedBody {
            return ResolvedBody(raw.id).apply { applyValuesFrom(raw) }
        }
    }
}
