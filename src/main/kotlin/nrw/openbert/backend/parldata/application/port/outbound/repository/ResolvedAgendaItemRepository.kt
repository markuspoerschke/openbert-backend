package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedAgendaItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ResolvedAgendaItemRepository : JpaRepository<ResolvedAgendaItem, UUID> {
    fun findByResource(resource: OparlResource): ResolvedAgendaItem?

    fun findAllByBodyIdOrderById(
        bodyId: UUID,
        pageable: Pageable,
    ): Page<ResolvedAgendaItem>

    fun findByIdAndBodyId(
        id: UUID,
        bodyId: UUID,
    ): Optional<ResolvedAgendaItem>
}
