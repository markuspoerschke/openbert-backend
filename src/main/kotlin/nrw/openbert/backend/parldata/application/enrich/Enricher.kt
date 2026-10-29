package nrw.openbert.backend.parldata.application.enrich

import nrw.openbert.backend.parldata.application.port.outbound.repository.EnrichTargetRepository
import nrw.openbert.backend.parldata.application.resolve.ResolveTargetEnqueuer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class Enricher(
    private val targetRepository: EnrichTargetRepository,
    entityEnrichers: List<EntityEnricher>,
    private val resolveTargetEnqueuer: ResolveTargetEnqueuer,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val entityEnrichers = entityEnrichers.associateBy(EntityEnricher::entityType)

    @Transactional
    fun run(targetId: UUID) {
        val target =
            targetRepository.findById(targetId).orElseThrow {
                IllegalArgumentException("Enrich target with id $targetId does not exist")
            }

        logger.info("Enriching {} raw entity {}", target.entityType, target.rawEntityId)
        // Entity types without an enricher pass through until enrichment is implemented.
        entityEnrichers[target.entityType]?.enrich(target.rawEntityId)
        resolveTargetEnqueuer.enqueue(target.rawEntityId, target.entityType)
        targetRepository.save(target.markAsEnriched())
    }
}
