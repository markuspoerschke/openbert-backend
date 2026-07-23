package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.oparl.domain.entity.AbstractMeeting
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawMeeting
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_resolved_meeting")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class ResolvedMeeting(resource: OparlResource) : AbstractMeeting(resource) {
    companion object {
        fun fromRaw(raw: RawMeeting): ResolvedMeeting {
            return ResolvedMeeting(raw.resource).apply { applyValuesFrom(raw) }
        }
    }
}
