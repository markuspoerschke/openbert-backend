package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.oparl.domain.entity.AbstractOrganization
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawOrganization
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_resolved_organization")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class ResolvedOrganization(resource: OparlResource) : AbstractOrganization(resource) {
    companion object {
        fun fromRaw(raw: RawOrganization): ResolvedOrganization {
            return ResolvedOrganization(raw.resource).apply { applyValuesFrom(raw) }
        }
    }
}
