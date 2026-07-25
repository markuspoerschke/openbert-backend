package nrw.openbert.backend.oparl.infrastructure.persistence.repository

import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedMeeting
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ResolvedMeetingRepository : JpaRepository<ResolvedMeeting, UUID> {
    fun findAllByBody_IdOrderById(bodyId: UUID): List<ResolvedMeeting>

    fun findByIdAndBody_Id(id: UUID, bodyId: UUID): Optional<ResolvedMeeting>
}
