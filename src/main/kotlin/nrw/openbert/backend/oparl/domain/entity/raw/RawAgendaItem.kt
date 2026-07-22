package nrw.openbert.backend.oparl.domain.entity.raw

import jakarta.persistence.Entity
import nrw.openbert.backend.oparl.domain.entity.AbstractAgendaItem
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_raw_agendaItem")
class RawAgendaItem(systemId: UUID, url: OparlUrl) : AbstractAgendaItem(systemId, url)
