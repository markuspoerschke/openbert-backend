package nrw.openbert.backend.parldata.adapter.inbound.scheduler

import nrw.openbert.backend.parldata.application.enrich.Enricher
import nrw.openbert.backend.parldata.application.port.outbound.repository.EnrichTargetRepository
import org.jobrunr.jobs.annotations.Job
import org.jobrunr.jobs.annotations.Recurring
import org.jobrunr.scheduling.JobScheduler
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ScheduleDueEnriches(
    private val enrichTargetRepository: EnrichTargetRepository,
    private val enricher: Enricher,
    private val jobScheduler: JobScheduler,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    @Job(name = "Enricher - Schedule")
    @Recurring(id = "enrich-schedule", cron = "PT15S")
    fun run() {
        logger.info("Running enrichment scheduler")

        val targets = enrichTargetRepository.findDue(
            Instant.now(),
            PageRequest.of(0, BATCH_SIZE),
        ).onEach { target ->
            val jobId = jobScheduler.enqueue { enricher.run(target.id) }
            target.markAsScheduled(jobId.asUUID())
        }

        enrichTargetRepository.saveAll(targets)
        logger.info("Scheduled {} enrich targets", targets.size)
    }

    companion object {
        const val BATCH_SIZE = 30
    }
}
