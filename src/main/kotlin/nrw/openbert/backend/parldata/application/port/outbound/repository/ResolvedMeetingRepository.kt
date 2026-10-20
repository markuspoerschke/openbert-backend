package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedMeeting
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ResolvedMeetingRepository : JpaRepository<ResolvedMeeting, UUID> {
    fun findByResource(resource: OparlResource): ResolvedMeeting?

    fun findAllByBody_IdOrderById(bodyId: UUID, pageable: Pageable): Page<ResolvedMeeting>

    fun findAllDistinctByOrganizations_IdAndBody_IdOrderById(
        organizationId: UUID,
        bodyId: UUID,
        pageable: Pageable,
    ): Page<ResolvedMeeting>

    fun findByIdAndBody_Id(id: UUID, bodyId: UUID): Optional<ResolvedMeeting>
}
