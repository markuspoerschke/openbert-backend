package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.domain.enums.ResolveEntityType
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AgendaItemResolver(
    private val resolvedEntityService: ResolvedEntityService,
) : EntityResolver {
    override val entityType = ResolveEntityType.AGENDA_ITEM

    override fun resolve(rawEntityId: UUID) {
        resolvedEntityService.resolveAgendaItem(rawEntityId)
    }
}
