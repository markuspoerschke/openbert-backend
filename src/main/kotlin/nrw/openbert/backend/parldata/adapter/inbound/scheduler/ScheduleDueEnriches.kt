package nrw.openbert.backend.parldata.adapter.inbound.scheduler

import nrw.openbert.backend.parldata.application.enrich.Enricher
import nrw.openbert.backend.parldata.application.port.outbound.repository.EnrichTargetRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant

@Service
@ConditionalOnProperty(name = ["app.enrich.scheduler.enabled"], havingValue = "true", matchIfMissing = true)
class ScheduleDueEnriches(
    private val enrichTargetRepository: EnrichTargetRepository,
    private val enricher: Enricher,
    private val failureRecorder: EnrichFailureRecorder,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Suppress("TooGenericExceptionCaught")
    @Scheduled(cron = "\${app.enrich.scheduler.cron:*/15 * * * * *}")
    fun run() {
        logger.info("Running enrichment scheduler")

        val dueTargets =
            enrichTargetRepository.findDue(
                Instant.now(),
                PageRequest.of(0, BATCH_SIZE),
            )

        dueTargets.forEach { target ->
            try {
                enricher.run(target.id)
            } catch (error: Exception) {
                failureRecorder.record(target.id, error)
            }
        }

        logger.info("Enriched {} enrich targets", dueTargets.size)
    }

    companion object {
        const val BATCH_SIZE = 100
    }
}
