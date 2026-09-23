package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.domain.enums.ResolveEntityType
import java.util.UUID

interface EntityResolver {
    val entityType: ResolveEntityType

    fun resolve(rawEntityId: UUID)
}
