package nrw.openbert.backend.resolve.application

import nrw.openbert.backend.resolve.domain.repository.ResolveTargetRepository
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
            PageRequest.of(0, 30),
        ).onEach { target ->
            val jobId = jobScheduler.enqueue { resolver.run(target.id) }
            target.markAsScheduled(jobId.asUUID())
        }

        resolveTargetRepository.saveAll(targets)
        logger.info("Scheduled {} resolve targets", targets.size)
    }
}
