package nrw.openbert.backend.oparl.domain.repository.raw

import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawConsultation
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RawConsultationRepository : JpaRepository<RawConsultation, UUID> {
    fun findByResource(resource: OparlResource): RawConsultation?
}
