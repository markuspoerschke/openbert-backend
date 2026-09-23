package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawSystem
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RawSystemRepository : JpaRepository<RawSystem, UUID> {
    fun findByResource(resource: OparlResource): RawSystem?
}
