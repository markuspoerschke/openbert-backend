package nrw.openbert.backend.oparl.infrastructure.persistence.repository

import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedBody
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ResolvedBodyRepository : JpaRepository<ResolvedBody, UUID> {
    fun findByResource(resource: OparlResource): ResolvedBody?

    fun findAllByOrderById(): List<ResolvedBody>
}
