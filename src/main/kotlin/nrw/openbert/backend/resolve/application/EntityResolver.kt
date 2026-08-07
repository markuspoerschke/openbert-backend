package nrw.openbert.backend.resolve.application

import nrw.openbert.backend.resolve.domain.entity.ResolveEntityType
import java.util.UUID

interface EntityResolver {
    val entityType: ResolveEntityType

    fun resolve(rawEntityId: UUID)
}
