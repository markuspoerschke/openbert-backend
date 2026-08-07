package nrw.openbert.backend.resolve.application

import nrw.openbert.backend.resolve.domain.entity.ResolveEntityType
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class LegislativeTermResolver(
    private val resolvedEntityService: ResolvedEntityService,
) : EntityResolver {
    override val entityType = ResolveEntityType.LEGISLATIVE_TERM

    override fun resolve(rawEntityId: UUID) {
        resolvedEntityService.resolveLegislativeTerm(rawEntityId)
    }
}
