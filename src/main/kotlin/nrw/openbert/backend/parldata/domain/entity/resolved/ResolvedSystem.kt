package nrw.openbert.backend.parldata.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.parldata.domain.entity.AbstractSystem
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawSystem

@Entity(name = "oparl_resolved_system")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class ResolvedSystem(resource: OparlResource) : AbstractSystem(resource) {
    companion object {
        fun fromRaw(raw: RawSystem): ResolvedSystem {
            return ResolvedSystem(raw.resource).apply { applyValuesFrom(raw) }
        }
    }
}
