package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedConsultation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional
import java.util.UUID

interface ResolvedConsultationRepository :
    JpaRepository<ResolvedConsultation, UUID>,
    JpaSpecificationExecutor<ResolvedConsultation> {
    fun findByResource(resource: OparlResource): ResolvedConsultation?

    fun findByIdAndBodyId(
        id: UUID,
        bodyId: UUID,
    ): Optional<ResolvedConsultation>
}
