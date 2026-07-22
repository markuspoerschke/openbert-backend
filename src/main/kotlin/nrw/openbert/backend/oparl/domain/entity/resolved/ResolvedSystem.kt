package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractSystem
import nrw.openbert.backend.domain.oparl.entity.raw.RawSystem
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_resolved_system")
class ResolvedSystem(systemId: UUID, url: OparlUrl) : AbstractSystem(systemId, url) {
    companion object {
        fun fromRaw(raw: RawSystem): ResolvedSystem {
            return ResolvedSystem(raw.systemId, raw.url).apply { applyValuesFrom(raw) }
        }
    }
}
