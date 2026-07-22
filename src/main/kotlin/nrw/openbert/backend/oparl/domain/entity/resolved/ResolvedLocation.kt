package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.oparl.domain.entity.AbstractLocation
import nrw.openbert.backend.oparl.domain.entity.raw.RawLocation
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_resolved_location")
class ResolvedLocation(systemId: UUID, url: OparlUrl) : AbstractLocation(systemId, url) {
    companion object {
        fun fromRaw(raw: RawLocation): ResolvedLocation {
            return ResolvedLocation(raw.systemId, raw.url).apply { applyValuesFrom(raw) }
        }
    }
}
