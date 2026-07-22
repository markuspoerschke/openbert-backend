package nrw.openbert.backend.oparl.domain.entity.raw

import jakarta.persistence.Entity
import nrw.openbert.backend.oparl.domain.entity.AbstractMeeting
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_raw_meeting")
class RawMeeting(systemId: UUID, url: OparlUrl) : AbstractMeeting(systemId, url)
