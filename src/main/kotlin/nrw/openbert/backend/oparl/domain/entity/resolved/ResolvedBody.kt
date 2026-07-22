package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractBody
import nrw.openbert.backend.domain.oparl.entity.raw.RawBody
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_resolved_body")
class ResolvedBody(systemId: UUID, url: OparlUrl) : AbstractBody(systemId, url) {
    companion object {
        fun fromRaw(raw: RawBody): ResolvedBody {
            return ResolvedBody(raw.systemId, raw.url).apply { applyValuesFrom(raw) }
        }
    }
}
