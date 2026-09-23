package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawConsultation
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RawConsultationRepository : JpaRepository<RawConsultation, UUID> {
    fun findByResource(resource: OparlResource): RawConsultation?
}
