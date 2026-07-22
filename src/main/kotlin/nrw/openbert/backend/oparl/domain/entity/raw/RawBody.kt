package nrw.openbert.backend.oparl.domain.entity.raw

import jakarta.persistence.Entity
import nrw.openbert.backend.oparl.domain.entity.AbstractBody
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_raw_body")
class RawBody(systemId: UUID, url: OparlUrl) : AbstractBody(systemId, url)
