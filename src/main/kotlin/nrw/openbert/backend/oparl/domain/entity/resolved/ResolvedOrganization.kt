package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.oparl.domain.entity.AbstractOrganization
import nrw.openbert.backend.oparl.domain.entity.raw.RawOrganization
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_resolved_organization")
class ResolvedOrganization(systemId: UUID, url: OparlUrl) : AbstractOrganization(systemId, url) {
    companion object {
        fun fromRaw(raw: RawOrganization): ResolvedOrganization {
            return ResolvedOrganization(raw.systemId, raw.url).apply { applyValuesFrom(raw) }
        }
    }
}
