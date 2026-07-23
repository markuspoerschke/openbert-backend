package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.oparl.domain.entity.AbstractLegislativeTerm
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawLegislativeTerm
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_resolved_legislativeTerm")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class ResolvedLegislativeTerm(resource: OparlResource) : AbstractLegislativeTerm(resource) {
    companion object {
        fun fromRaw(raw: RawLegislativeTerm): ResolvedLegislativeTerm {
            return ResolvedLegislativeTerm(raw.resource).apply { applyValuesFrom(raw) }
        }
    }
}
