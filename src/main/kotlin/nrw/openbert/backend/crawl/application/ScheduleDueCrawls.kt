package nrw.openbert.backend.crawl.application

import nrw.openbert.backend.crawl.domain.repository.CrawlTargetRepository
import org.jobrunr.jobs.annotations.Job
import org.jobrunr.jobs.annotations.Recurring
import org.jobrunr.scheduling.JobScheduler
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ScheduleDueCrawls(
    private val crawlTargetRepository: CrawlTargetRepository,
    private val crawler: Crawler,
    private val jobScheduler: JobScheduler,
) {
    @Transactional
    @Job(name = "Crawler - Schedule")
    @Recurring(id = "crawl-schedule", cron = "PT15S")
    fun run() {
        val dueTargets = crawlTargetRepository.findDue(Instant.now()).onEach { crawlTarget ->
            val jobId = jobScheduler.enqueue {
                crawler.run(crawlTarget.id)
            }
            crawlTarget.markAsScheduled(jobId.asUUID())
        }

        crawlTargetRepository.saveAll(dueTargets)
    }
}
