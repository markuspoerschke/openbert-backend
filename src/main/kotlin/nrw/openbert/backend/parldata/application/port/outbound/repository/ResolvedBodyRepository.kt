package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedBody
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ResolvedBodyRepository : JpaRepository<ResolvedBody, UUID> {
    fun findByResource(resource: OparlResource): ResolvedBody?

    fun findAllByOrderById(): List<ResolvedBody>
}
