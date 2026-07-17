package nrw.openbert.backend.domain.oparl.entity.raw

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractFile
import nrw.openbert.backend.domain.oparl.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_raw_file")
class RawFile(systemId: UUID, url: OparlUrl) : AbstractFile(systemId, url)
