package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedBody
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.UUID

interface ResolvedBodyRepository :
    JpaRepository<ResolvedBody, UUID>,
    JpaSpecificationExecutor<ResolvedBody> {
    fun findByResource(resource: OparlResource): ResolvedBody?
}
