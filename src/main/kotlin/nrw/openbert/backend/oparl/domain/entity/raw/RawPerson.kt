package nrw.openbert.backend.oparl.domain.entity.raw

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractPerson
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_raw_person")
class RawPerson(systemId: UUID, url: OparlUrl) : AbstractPerson(systemId, url)
