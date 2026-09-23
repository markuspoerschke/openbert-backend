package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.domain.enums.ResolveEntityType
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolveTargetRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class Resolver(
    private val targetRepository: ResolveTargetRepository,
    entityResolvers: List<EntityResolver>,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val entityResolvers = entityResolvers.associateBy(EntityResolver::entityType)

    val supportedEntityTypes: Set<ResolveEntityType>
        get() = entityResolvers.keys

    @Transactional
    fun run(targetId: UUID) {
        val target = targetRepository.findById(targetId).orElseThrow {
            IllegalArgumentException("Resolve target with id $targetId does not exist")
        }
        val entityResolver = entityResolvers[target.entityType]
            ?: throw UnsupportedOperationException("No resolver exists for ${target.entityType}")

        logger.info("Resolving {} raw entity {}", target.entityType, target.rawEntityId)
        entityResolver.resolve(target.rawEntityId)
        targetRepository.save(target.markAsResolved())
    }
}
