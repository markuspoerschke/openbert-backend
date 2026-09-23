package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.domain.enums.ResolveEntityType
import nrw.openbert.backend.parldata.domain.entity.ResolveTarget
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolveTargetRepository
import nrw.openbert.backend.parldata.domain.entity.AbstractOparlEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ResolveTargetEnqueuer(
    private val repository: ResolveTargetRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun enqueue(entity: AbstractOparlEntity) {
        val entityType = ResolveEntityType.from(entity)
        logger.info("Enqueuing {} raw entity {} for resolving", entityType, entity.id)

        val target = repository.findByRawEntityIdAndEntityType(entity.id, entityType)
            ?: ResolveTarget.create(entity)
        repository.save(target.enqueue())
    }
}
