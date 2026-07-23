package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.oparl.domain.entity.AbstractConsultation
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawConsultation
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_resolved_consultation")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class ResolvedConsultation(resource: OparlResource) : AbstractConsultation(resource) {
    companion object {
        fun fromRaw(raw: RawConsultation): ResolvedConsultation {
            return ResolvedConsultation(raw.resource).apply { applyValuesFrom(raw) }
        }
    }
}
