package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedMeeting
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional
import java.util.UUID

interface ResolvedMeetingRepository :
    JpaRepository<ResolvedMeeting, UUID>,
    JpaSpecificationExecutor<ResolvedMeeting> {
    fun findByResource(resource: OparlResource): ResolvedMeeting?

    fun findByIdAndBodyId(
        id: UUID,
        bodyId: UUID,
    ): Optional<ResolvedMeeting>
}
