package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.domain.enums.OparlType
import java.util.UUID

interface EntityResolver {
    val entityType: OparlType

    fun resolve(rawEntityId: UUID)
}
