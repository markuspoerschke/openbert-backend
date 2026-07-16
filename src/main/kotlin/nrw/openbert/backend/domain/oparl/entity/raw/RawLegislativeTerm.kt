package nrw.openbert.backend.domain.oparl.entity.raw

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractLegislativeTerm
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId

@Entity(name = "oparl_raw_legislativeTerm")
class RawLegislativeTerm(id: OparlEntityId) : AbstractLegislativeTerm(id)
