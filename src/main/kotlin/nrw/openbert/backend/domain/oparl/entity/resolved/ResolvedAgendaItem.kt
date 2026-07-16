package nrw.openbert.backend.domain.oparl.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractAgendaItem
import nrw.openbert.backend.domain.oparl.entity.raw.RawAgendaItem
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId

@Entity(name = "oparl_resolved_agendaItem")
class ResolvedAgendaItem(id: OparlEntityId) : AbstractAgendaItem(id) {
    companion object {
        fun fromRaw(raw: RawAgendaItem): ResolvedAgendaItem {
            return ResolvedAgendaItem(raw.id).apply { applyValuesFrom(raw) }
        }
    }
}
