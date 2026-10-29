package nrw.openbert.backend.parldata.application.enrich

import nrw.openbert.backend.parldata.application.port.outbound.repository.EnrichTargetRepository
import nrw.openbert.backend.parldata.domain.entity.AbstractOparlEntity
import nrw.openbert.backend.parldata.domain.entity.EnrichTarget
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class EnrichTargetEnqueuer(
    private val repository: EnrichTargetRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun enqueue(entity: AbstractOparlEntity) {
        val entityType = entity.getType()
        logger.info("Enqueuing {} raw entity {} for enrichment", entityType, entity.id)

        val target =
            repository.findByRawEntityIdAndEntityType(entity.id, entityType)
                ?: EnrichTarget.create(entity)
        repository.save(target.enqueue())
    }
}
