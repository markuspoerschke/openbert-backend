package nrw.openbert.backend.oparl.domain.repository.raw

import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawAgendaItem
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RawAgendaItemRepository : JpaRepository<RawAgendaItem, UUID> {
    fun findByResource(resource: OparlResource): RawAgendaItem?
}
