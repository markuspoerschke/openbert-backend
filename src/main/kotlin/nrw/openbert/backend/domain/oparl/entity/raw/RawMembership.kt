package nrw.openbert.backend.domain.oparl.entity.raw

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractMembership
import nrw.openbert.backend.domain.oparl.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_raw_membership")
class RawMembership(systemId: UUID, url: OparlUrl) : AbstractMembership(systemId, url)
