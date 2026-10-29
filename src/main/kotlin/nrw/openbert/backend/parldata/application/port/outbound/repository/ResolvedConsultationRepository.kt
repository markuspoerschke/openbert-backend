package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedConsultation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ResolvedConsultationRepository : JpaRepository<ResolvedConsultation, UUID> {
    fun findByResource(resource: OparlResource): ResolvedConsultation?

    fun findAllByBodyIdOrderById(
        bodyId: UUID,
        pageable: Pageable,
    ): Page<ResolvedConsultation>

    fun findAllDistinctByOrganizationsIdAndBodyIdOrderById(
        organizationId: UUID,
        bodyId: UUID,
        pageable: Pageable,
    ): Page<ResolvedConsultation>

    fun findByIdAndBodyId(
        id: UUID,
        bodyId: UUID,
    ): Optional<ResolvedConsultation>
}
