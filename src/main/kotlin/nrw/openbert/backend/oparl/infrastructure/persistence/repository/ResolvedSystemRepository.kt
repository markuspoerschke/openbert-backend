package nrw.openbert.backend.oparl.infrastructure.persistence.repository

import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedSystem
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ResolvedSystemRepository : JpaRepository<ResolvedSystem, UUID> {
    fun findByResource(resource: OparlResource): ResolvedSystem?
}
