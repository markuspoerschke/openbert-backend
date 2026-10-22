package nrw.openbert.backend.parldata.adapter.inbound.scheduler

import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolveTargetRepository
import nrw.openbert.backend.parldata.application.resolve.Resolver
import org.jobrunr.jobs.annotations.Job
import org.jobrunr.jobs.annotations.Recurring
import org.jobrunr.scheduling.JobScheduler
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ScheduleDueResolves(
    private val resolveTargetRepository: ResolveTargetRepository,
    private val resolver: Resolver,
    private val jobScheduler: JobScheduler,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    @Job(name = "Resolver - Schedule")
    @Recurring(id = "resolve-schedule", cron = "PT15S")
    fun run() {
        logger.info("Running resolve scheduler")

        val targets = resolveTargetRepository.findDue(
            Instant.now(),
            resolver.supportedEntityTypes,
            PageRequest.of(0, BATCH_SIZE),
        ).onEach { target ->
            val jobId = jobScheduler.enqueue { resolver.run(target.id) }
            target.markAsScheduled(jobId.asUUID())
        }

        resolveTargetRepository.saveAll(targets)
        logger.info("Scheduled {} resolve targets", targets.size)
    }

    companion object {
        const val BATCH_SIZE = 100
    }
}
