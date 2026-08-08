package nrw.openbert.backend.crawl.application

import nrw.openbert.backend.crawl.domain.repository.CrawlTargetRepository
import org.jobrunr.jobs.annotations.Job
import org.jobrunr.jobs.annotations.Recurring
import org.jobrunr.scheduling.JobScheduler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ScheduleDueCrawls(
    private val crawlTargetRepository: CrawlTargetRepository,
    private val crawler: Crawler,
    private val jobScheduler: JobScheduler,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    @Job(name = "Crawler - Schedule")
    @Recurring(id = "crawl-schedule", cron = "PT30S")
    fun run() {
        logger.info("Running crawl scheduler")

        val dueTargets = crawlTargetRepository.findDue(Instant.now(), 1).onEach { crawlTarget ->
            val jobId = jobScheduler.enqueue {
                crawler.run(crawlTarget.id)
            }
            crawlTarget.markAsScheduled(jobId.asUUID())
        }

        crawlTargetRepository.saveAll(dueTargets)
        logger.info("Scheduled {} crawl targets", dueTargets.size)
    }
}
