package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractMeeting
import nrw.openbert.backend.domain.oparl.entity.raw.RawMeeting
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_resolved_meeting")
class ResolvedMeeting(systemId: UUID, url: OparlUrl) : AbstractMeeting(systemId, url) {
    companion object {
        fun fromRaw(raw: RawMeeting): ResolvedMeeting {
            return ResolvedMeeting(raw.systemId, raw.url).apply { applyValuesFrom(raw) }
        }
    }
}
