package nrw.openbert.backend.domain.oparl.entity.raw

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractBody
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId

@Entity(name = "oparl_raw_body")
class RawBody(id: OparlEntityId) : AbstractBody(id)
