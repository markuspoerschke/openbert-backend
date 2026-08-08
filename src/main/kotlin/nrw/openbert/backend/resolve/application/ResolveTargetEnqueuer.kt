package nrw.openbert.backend.resolve.application

import nrw.openbert.backend.resolve.domain.entity.ResolveEntityType
import nrw.openbert.backend.resolve.domain.entity.ResolveTarget
import nrw.openbert.backend.resolve.domain.repository.ResolveTargetRepository
import nrw.openbert.backend.oparl.domain.entity.AbstractOparlEntity
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
