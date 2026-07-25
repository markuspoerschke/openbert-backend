package nrw.openbert.backend.oparl.infrastructure.persistence.repository

import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedAgendaItem
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ResolvedAgendaItemRepository : JpaRepository<ResolvedAgendaItem, UUID> {
    fun findAllByBody_IdOrderById(bodyId: UUID): List<ResolvedAgendaItem>

    fun findByIdAndBody_Id(id: UUID, bodyId: UUID): Optional<ResolvedAgendaItem>
}
