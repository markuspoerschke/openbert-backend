package nrw.openbert.backend.oparl.domain.entity.raw

import jakarta.persistence.Entity
import nrw.openbert.backend.oparl.domain.entity.AbstractSystem
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_raw_system")
class RawSystem(systemId: UUID, url: OparlUrl) : AbstractSystem(systemId, url)
