package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.oparl.domain.entity.AbstractPaper
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawPaper
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_resolved_paper")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class ResolvedPaper(resource: OparlResource) : AbstractPaper(resource) {
    companion object {
        fun fromRaw(raw: RawPaper): ResolvedPaper {
            return ResolvedPaper(raw.resource).apply { applyValuesFrom(raw) }
        }
    }
}
