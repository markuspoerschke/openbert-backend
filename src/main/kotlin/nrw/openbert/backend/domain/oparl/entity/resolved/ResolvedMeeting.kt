package nrw.openbert.backend.domain.oparl.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractMeeting
import nrw.openbert.backend.domain.oparl.entity.raw.RawMeeting
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId

@Entity(name = "oparl_resolved_meeting")
class ResolvedMeeting(id: OparlEntityId) : AbstractMeeting(id) {
    companion object {
        fun fromRaw(raw: RawMeeting): ResolvedMeeting {
            return ResolvedMeeting(raw.id).apply { applyValuesFrom(raw) }
        }
    }
}
