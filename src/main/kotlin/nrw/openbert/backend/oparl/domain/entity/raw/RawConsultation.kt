package nrw.openbert.backend.oparl.domain.entity.raw

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractConsultation
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_raw_consultation")
class RawConsultation(systemId: UUID, url: OparlUrl) : AbstractConsultation(systemId, url)
