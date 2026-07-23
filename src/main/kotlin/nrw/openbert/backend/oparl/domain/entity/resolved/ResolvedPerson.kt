package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.oparl.domain.entity.AbstractPerson
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawPerson
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_resolved_person")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class ResolvedPerson(resource: OparlResource) : AbstractPerson(resource) {
    companion object {
        fun fromRaw(raw: RawPerson): ResolvedPerson {
            return ResolvedPerson(raw.resource).apply { applyValuesFrom(raw) }
        }
    }
}
