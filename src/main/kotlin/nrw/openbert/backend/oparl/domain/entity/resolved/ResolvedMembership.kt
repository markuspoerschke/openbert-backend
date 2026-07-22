package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractMembership
import nrw.openbert.backend.domain.oparl.entity.raw.RawMembership
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_resolved_membership")
class ResolvedMembership(systemId: UUID, url: OparlUrl) : AbstractMembership(systemId, url) {
    companion object {
        fun fromRaw(raw: RawMembership): ResolvedMembership {
            return ResolvedMembership(raw.systemId, raw.url).apply { applyValuesFrom(raw) }
        }
    }
}
