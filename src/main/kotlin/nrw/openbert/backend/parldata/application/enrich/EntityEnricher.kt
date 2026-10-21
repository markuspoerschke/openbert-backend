package nrw.openbert.backend.parldata.application.enrich

import nrw.openbert.backend.parldata.domain.enums.OparlType
import java.util.UUID

interface EntityEnricher {
    val entityType: OparlType
    fun enrich(rawEntityId: UUID)
}
