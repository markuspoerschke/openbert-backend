package nrw.openbert.backend.domain.oparl.entity.raw

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractSystem
import nrw.openbert.backend.domain.oparl.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_raw_system")
class RawSystem(systemId: UUID, url: OparlUrl) : AbstractSystem(systemId, url)
