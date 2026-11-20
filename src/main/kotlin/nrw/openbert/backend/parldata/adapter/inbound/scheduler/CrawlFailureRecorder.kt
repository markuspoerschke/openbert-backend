package nrw.openbert.backend.parldata.adapter.inbound.scheduler

import io.sentry.IScope
import io.sentry.IScopes
import io.sentry.Sentry
import nrw.openbert.backend.parldata.application.port.outbound.repository.CrawlTargetRepository
import nrw.openbert.backend.parldata.domain.entity.CrawlTarget
import org.springframework.stereotype.Component

@Component
class CrawlFailureRecorder(
    crawlTargetRepository: CrawlTargetRepository,
    scopes: IScopes = Sentry.getCurrentScopes(),
) : QueueFailureRecorder<CrawlTarget>(crawlTargetRepository, scopes) {
    override val queue = "crawl"

    override fun configureScope(
        scope: IScope,
        target: CrawlTarget,
    ) {
        scope.setExtra("crawl.target.url", target.resource.url.value)
    }
}
