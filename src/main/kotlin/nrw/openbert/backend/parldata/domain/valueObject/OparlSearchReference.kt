package nrw.openbert.backend.parldata.domain.valueObject

import nrw.openbert.backend.parldata.domain.enums.OparlType
import java.util.UUID

data class OparlSearchReference(
    val entityType: OparlType,
    val entityId: UUID,
)