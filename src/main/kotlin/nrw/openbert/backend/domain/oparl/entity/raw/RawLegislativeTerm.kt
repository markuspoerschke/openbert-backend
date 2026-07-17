package nrw.openbert.backend.domain.oparl.entity.raw

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractLegislativeTerm
import nrw.openbert.backend.domain.oparl.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_raw_legislativeTerm")
class RawLegislativeTerm(systemId: UUID, url: OparlUrl) : AbstractLegislativeTerm(systemId, url)
