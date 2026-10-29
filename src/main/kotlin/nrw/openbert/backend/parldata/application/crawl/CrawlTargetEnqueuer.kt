package nrw.openbert.backend.parldata.application.crawl

import nrw.openbert.backend.parldata.application.port.outbound.repository.CrawlTargetRepository
import nrw.openbert.backend.parldata.domain.entity.CrawlTarget
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.enums.OparlType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CrawlTargetEnqueuer(
    private val repository: CrawlTargetRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun enqueue(
        resource: OparlResource,
        entityType: OparlType? = null,
    ) {
        logger.info("Enqueuing OParl resource {}", resource.url.value)

        val crawlTarget = findOrCreate(resource, entityType)
        entityType?.let(crawlTarget::assignEntityType)
        crawlTarget.increaseReferenceCounter()

        repository.save(crawlTarget)
    }

    fun markAsCrawled(resource: OparlResource) {
        val crawlTarget = findOrCreate(resource)
        crawlTarget.markAsCrawled()

        repository.save(crawlTarget)
    }

    private fun findOrCreate(
        resource: OparlResource,
        entityType: OparlType? = null,
    ): CrawlTarget =
        repository.findByResource(resource)
            ?: CrawlTarget.create(resource, entityType)
}
