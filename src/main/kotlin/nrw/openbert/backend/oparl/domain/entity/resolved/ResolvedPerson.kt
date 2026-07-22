package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractPerson
import nrw.openbert.backend.domain.oparl.entity.raw.RawPerson
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_resolved_person")
class ResolvedPerson(systemId: UUID, url: OparlUrl) : AbstractPerson(systemId, url) {
    companion object {
        fun fromRaw(raw: RawPerson): ResolvedPerson {
            return ResolvedPerson(raw.systemId, raw.url).apply { applyValuesFrom(raw) }
        }
    }
}
