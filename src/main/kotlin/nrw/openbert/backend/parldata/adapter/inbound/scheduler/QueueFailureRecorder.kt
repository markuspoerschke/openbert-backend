package nrw.openbert.backend.parldata.adapter.inbound.scheduler

import io.sentry.IScope
import io.sentry.IScopes
import io.sentry.Sentry
import nrw.openbert.backend.parldata.domain.entity.QueueTarget
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

abstract class QueueFailureRecorder<T : QueueTarget>(
    private val repository: JpaRepository<T, UUID>,
    private val scopes: IScopes = Sentry.getCurrentScopes(),
) {
    protected abstract val queue: String

    protected abstract fun configureScope(
        scope: IScope,
        target: T,
    )

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    open fun record(
        targetId: UUID,
        error: Exception,
    ) {
        val target =
            repository
                .findById(targetId)
                .orElseThrow { IllegalArgumentException("Target with id $targetId does not exist") }

        logger.error("{} of target {} failed", queue, targetId, error)
        scopes.captureException(error) { scope ->
            scope.setTag("$queue.target.id", targetId.toString())
            configureScope(scope, target)
        }

        target.markAsFailed(error.message ?: error.javaClass.simpleName)
        repository.save(target)
    }
}
