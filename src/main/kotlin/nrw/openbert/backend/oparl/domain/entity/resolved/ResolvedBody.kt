package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.oparl.domain.entity.AbstractBody
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawBody
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.*

@Entity(name = "oparl_resolved_body")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class ResolvedBody(resource: OparlResource) : AbstractBody(resource) {
    companion object {
        fun fromRaw(raw: RawBody): ResolvedBody {
            return ResolvedBody(raw.resource).apply { applyValuesFrom(raw) }
        }
    }
}
