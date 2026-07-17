package nrw.openbert.backend.domain.oparl.entity.raw

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractMeeting
import nrw.openbert.backend.domain.oparl.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_raw_meeting")
class RawMeeting(systemId: UUID, url: OparlUrl) : AbstractMeeting(systemId, url)
