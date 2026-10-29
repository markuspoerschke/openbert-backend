package nrw.openbert.backend.parldata.adapter.inbound.scheduler

import io.sentry.IScopes
import io.sentry.Sentry
import nrw.openbert.backend.parldata.application.port.outbound.repository.CrawlTargetRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CrawlFailureRecorder(
    private val crawlTargetRepository: CrawlTargetRepository,
    private val scopes: IScopes = Sentry.getCurrentScopes(),
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun record(
        crawlTargetId: UUID,
        error: Exception,
    ) {
        logger.error("Crawl of target {} failed", crawlTargetId, error)

        val crawlTarget =
            crawlTargetRepository
                .findById(crawlTargetId)
                .orElseThrow { IllegalArgumentException("Target with id $crawlTargetId does not exist") }

        scopes.captureException(error) { scope ->
            scope.setTag("crawl.target.id", crawlTargetId.toString())
            scope.setExtra("crawl.target.url", crawlTarget.resource.url.value)
        }

        crawlTarget.markAsFailed(error.message ?: error.javaClass.simpleName)
        crawlTargetRepository.save(crawlTarget)
    }
}
