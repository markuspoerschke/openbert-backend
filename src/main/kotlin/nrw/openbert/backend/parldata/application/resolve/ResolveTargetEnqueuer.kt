package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolveTargetRepository
import nrw.openbert.backend.parldata.domain.entity.AbstractOparlEntity
import nrw.openbert.backend.parldata.domain.entity.ResolveTarget
import nrw.openbert.backend.parldata.domain.enums.OparlType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ResolveTargetEnqueuer(
    private val repository: ResolveTargetRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun enqueue(entity: AbstractOparlEntity) = enqueue(entity.id, entity.getType())

    fun enqueue(
        rawEntityId: UUID,
        entityType: OparlType,
    ) {
        logger.info("Enqueuing {} raw entity {} for resolving", entityType, rawEntityId)

        val target =
            repository.findByRawEntityIdAndEntityType(rawEntityId, entityType)
                ?: ResolveTarget(rawEntityId = rawEntityId, entityType = entityType)
        repository.save(target.enqueue())
    }
}
