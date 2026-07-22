package nrw.openbert.backend.oparl.domain.entity.raw

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractPaper
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_raw_paper")
class RawPaper(systemId: UUID, url: OparlUrl) : AbstractPaper(systemId, url)
