package nrw.openbert.backend.parldata.adapter.inbound.scheduler

import io.sentry.IScope
import io.sentry.IScopes
import io.sentry.Sentry
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolveTargetRepository
import nrw.openbert.backend.parldata.domain.entity.ResolveTarget
import org.springframework.stereotype.Component

@Component
class ResolveFailureRecorder(
    resolveTargetRepository: ResolveTargetRepository,
    scopes: IScopes = Sentry.getCurrentScopes(),
) : QueueFailureRecorder<ResolveTarget>(resolveTargetRepository, scopes) {
    override val queue = "resolve"

    override fun configureScope(
        scope: IScope,
        target: ResolveTarget,
    ) {
        scope.setExtra("resolve.target.rawEntityId", target.rawEntityId.toString())
        scope.setExtra("resolve.target.entityType", target.entityType.name)
    }
}
