package nrw.openbert.backend.parldata.adapter.inbound.scheduler

import nrw.openbert.backend.parldata.adapter.inbound.crawler.Crawler
import nrw.openbert.backend.parldata.application.port.outbound.repository.CrawlTargetRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
@ConditionalOnProperty(name = ["app.crawl.scheduler.enabled"], havingValue = "true", matchIfMissing = true)
class ScheduleDueCrawls(
    private val crawlTargetRepository: CrawlTargetRepository,
    private val crawler: Crawler,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    @Scheduled(cron = "\${app.crawl.scheduler.cron:0 * * * * *}")
    fun run() {
        logger.info("Running crawl scheduler")

        val dueTargets =
            crawlTargetRepository
                .findDue(Instant.now(), 1)
                .onEach { crawlTarget -> crawler.run(crawlTarget.id) }

        logger.info("Crawled {} crawl targets", dueTargets.size)
    }
}
