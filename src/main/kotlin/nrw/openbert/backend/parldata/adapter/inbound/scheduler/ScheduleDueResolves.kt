package nrw.openbert.backend.parldata.adapter.inbound.scheduler

import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolveTargetRepository
import nrw.openbert.backend.parldata.application.resolve.Resolver
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant

@Service
@ConditionalOnProperty(name = ["app.resolve.scheduler.enabled"], havingValue = "true", matchIfMissing = true)
class ScheduleDueResolves(
    private val resolveTargetRepository: ResolveTargetRepository,
    private val resolver: Resolver,
    private val failureRecorder: ResolveFailureRecorder,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Suppress("TooGenericExceptionCaught")
    @Scheduled(cron = "\${app.resolve.scheduler.cron:*/15 * * * * *}")
    fun run() {
        logger.info("Running resolve scheduler")

        val dueTargets =
            resolveTargetRepository.findDue(
                Instant.now(),
                resolver.supportedEntityTypes,
                PageRequest.of(0, BATCH_SIZE),
            )

        dueTargets.forEach { target ->
            try {
                resolver.run(target.id)
            } catch (error: Exception) {
                failureRecorder.record(target.id, error)
            }
        }

        logger.info("Resolved {} resolve targets", dueTargets.size)
    }

    companion object {
        const val BATCH_SIZE = 100
    }
}
