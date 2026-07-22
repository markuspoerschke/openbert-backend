package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractConsultation
import nrw.openbert.backend.domain.oparl.entity.raw.RawConsultation
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_resolved_consultation")
class ResolvedConsultation(systemId: UUID, url: OparlUrl) : AbstractConsultation(systemId, url) {
    companion object {
        fun fromRaw(raw: RawConsultation): ResolvedConsultation {
            return ResolvedConsultation(raw.systemId, raw.url).apply { applyValuesFrom(raw) }
        }
    }
}
