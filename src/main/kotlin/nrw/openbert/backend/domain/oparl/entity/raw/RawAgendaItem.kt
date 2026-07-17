package nrw.openbert.backend.domain.oparl.entity.raw

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractAgendaItem
import nrw.openbert.backend.domain.oparl.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_raw_agendaItem")
class RawAgendaItem(systemId: UUID, url: OparlUrl) : AbstractAgendaItem(systemId, url)
