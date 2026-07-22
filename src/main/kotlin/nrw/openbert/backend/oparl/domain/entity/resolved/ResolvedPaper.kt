package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.oparl.domain.entity.AbstractPaper
import nrw.openbert.backend.oparl.domain.entity.raw.RawPaper
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_resolved_paper")
class ResolvedPaper(systemId: UUID, url: OparlUrl) : AbstractPaper(systemId, url) {
    companion object {
        fun fromRaw(raw: RawPaper): ResolvedPaper {
            return ResolvedPaper(raw.systemId, raw.url).apply { applyValuesFrom(raw) }
        }
    }
}
