package nrw.openbert.backend.domain.oparl.entity.raw

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractSystem
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId

@Entity(name = "oparl_raw_system")
class RawSystem(id: OparlEntityId) : AbstractSystem(id)
