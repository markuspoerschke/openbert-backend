package nrw.openbert.backend.resolve.application

import nrw.openbert.backend.resolve.domain.entity.ResolveEntityType
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class LocationResolver(
    private val resolvedEntityService: ResolvedEntityService,
) : EntityResolver {
    override val entityType = ResolveEntityType.LOCATION

    override fun resolve(rawEntityId: UUID) {
        resolvedEntityService.resolveLocation(rawEntityId)
    }
}
