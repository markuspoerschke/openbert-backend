package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.oparl.domain.entity.AbstractAgendaItem
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawAgendaItem
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_resolved_agendaItem")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class ResolvedAgendaItem(resource: OparlResource) : AbstractAgendaItem(resource) {
    companion object {
        fun fromRaw(raw: RawAgendaItem): ResolvedAgendaItem {
            return ResolvedAgendaItem(raw.resource).apply { applyValuesFrom(raw) }
        }
    }
}
