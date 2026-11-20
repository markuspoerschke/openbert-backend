package nrw.openbert.backend.parldata.adapter.inbound.scheduler

import io.sentry.IScope
import io.sentry.IScopes
import io.sentry.Sentry
import nrw.openbert.backend.parldata.application.port.outbound.repository.EnrichTargetRepository
import nrw.openbert.backend.parldata.domain.entity.EnrichTarget
import org.springframework.stereotype.Component

@Component
class EnrichFailureRecorder(
    enrichTargetRepository: EnrichTargetRepository,
    scopes: IScopes = Sentry.getCurrentScopes(),
) : QueueFailureRecorder<EnrichTarget>(enrichTargetRepository, scopes) {
    override val queue = "enrich"

    override fun configureScope(
        scope: IScope,
        target: EnrichTarget,
    ) {
        scope.setExtra("enrich.target.rawEntityId", target.rawEntityId.toString())
        scope.setExtra("enrich.target.entityType", target.entityType.name)
    }
}
