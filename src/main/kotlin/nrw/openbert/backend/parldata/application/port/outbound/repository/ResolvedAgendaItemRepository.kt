package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedAgendaItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional
import java.util.UUID

interface ResolvedAgendaItemRepository :
    JpaRepository<ResolvedAgendaItem, UUID>,
    JpaSpecificationExecutor<ResolvedAgendaItem> {
    fun findByResource(resource: OparlResource): ResolvedAgendaItem?

    fun findByIdAndBodyId(
        id: UUID,
        bodyId: UUID,
    ): Optional<ResolvedAgendaItem>
}
