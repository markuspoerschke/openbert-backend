package nrw.openbert.backend.domain.oparl.entity.raw

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractConsultation
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId

@Entity(name = "oparl_raw_consultation")
class RawConsultation(id: OparlEntityId) : AbstractConsultation(id)
