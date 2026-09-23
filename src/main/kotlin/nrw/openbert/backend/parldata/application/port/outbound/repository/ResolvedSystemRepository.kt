package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedSystem
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ResolvedSystemRepository : JpaRepository<ResolvedSystem, UUID> {
    fun findByResource(resource: OparlResource): ResolvedSystem?
}
