package nrw.openbert.backend.crawl.application

import nrw.openbert.backend.crawl.domain.entity.CrawlTarget
import nrw.openbert.backend.crawl.domain.repository.CrawlTargetRepository
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CrawlTargetEnqueuer(
    private val repository: CrawlTargetRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun enqueue(resource: OparlResource) {
        logger.info("Enqueuing OParl resource {}", resource.url.value)

        val crawlTarget = findOrCreate(resource)
        crawlTarget.increaseReferenceCounter()
        repository.save(crawlTarget)
    }

    fun markAsCrawled(resource: OparlResource) {
        val crawlTarget = findOrCreate(resource)
        crawlTarget.markAsCrawled()

        repository.save(crawlTarget)
    }

    private fun findOrCreate(resource: OparlResource): CrawlTarget =
        repository.findByResource(resource)
            ?: CrawlTarget.create(resource)
}
