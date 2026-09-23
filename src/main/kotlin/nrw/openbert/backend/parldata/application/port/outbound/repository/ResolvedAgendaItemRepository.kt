package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedAgendaItem
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ResolvedAgendaItemRepository : JpaRepository<ResolvedAgendaItem, UUID> {
    fun findByResource(resource: OparlResource): ResolvedAgendaItem?

    fun findAllByBody_IdOrderById(bodyId: UUID): List<ResolvedAgendaItem>

    fun findByIdAndBody_Id(id: UUID, bodyId: UUID): Optional<ResolvedAgendaItem>
}
