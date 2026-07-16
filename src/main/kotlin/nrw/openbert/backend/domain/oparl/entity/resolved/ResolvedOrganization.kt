package nrw.openbert.backend.domain.oparl.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractOrganization
import nrw.openbert.backend.domain.oparl.entity.raw.RawOrganization
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId

@Entity(name = "oparl_resolved_organization")
class ResolvedOrganization(id: OparlEntityId) : AbstractOrganization(id) {
    companion object {
        fun fromRaw(raw: RawOrganization): ResolvedOrganization {
            return ResolvedOrganization(raw.id).apply { applyValuesFrom(raw) }
        }
    }
}
