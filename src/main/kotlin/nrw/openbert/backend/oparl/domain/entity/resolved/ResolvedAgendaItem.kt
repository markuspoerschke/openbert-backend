package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.oparl.domain.entity.AbstractAgendaItem
import nrw.openbert.backend.oparl.domain.entity.raw.RawAgendaItem
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_resolved_agendaItem")
class ResolvedAgendaItem(systemId: UUID, url: OparlUrl) : AbstractAgendaItem(systemId, url) {
    companion object {
        fun fromRaw(raw: RawAgendaItem): ResolvedAgendaItem {
            return ResolvedAgendaItem(raw.systemId, raw.url).apply { applyValuesFrom(raw) }
        }
    }
}
