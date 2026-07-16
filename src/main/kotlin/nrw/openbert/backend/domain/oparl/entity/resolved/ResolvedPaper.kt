package nrw.openbert.backend.domain.oparl.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractPaper
import nrw.openbert.backend.domain.oparl.entity.raw.RawPaper
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId

@Entity(name = "oparl_resolved_paper")
class ResolvedPaper(id: OparlEntityId) : AbstractPaper(id) {
    companion object {
        fun fromRaw(raw: RawPaper): ResolvedPaper {
            return ResolvedPaper(raw.id).apply { applyValuesFrom(raw) }
        }
    }
}
