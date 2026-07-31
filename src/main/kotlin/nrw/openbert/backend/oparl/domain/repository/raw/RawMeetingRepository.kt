package nrw.openbert.backend.oparl.domain.repository.raw

import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawMeeting
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RawMeetingRepository : JpaRepository<RawMeeting, UUID> {
    fun findByResource(resource: OparlResource): RawMeeting?
}
