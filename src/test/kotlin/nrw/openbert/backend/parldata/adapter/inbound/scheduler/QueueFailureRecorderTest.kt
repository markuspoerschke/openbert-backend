package nrw.openbert.backend.parldata.adapter.inbound.scheduler

import io.sentry.IScope
import io.sentry.IScopes
import io.sentry.ScopeCallback
import nrw.openbert.backend.parldata.application.port.outbound.repository.CrawlTargetRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.EnrichTargetRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolveTargetRepository
import nrw.openbert.backend.parldata.domain.entity.CrawlTarget
import nrw.openbert.backend.parldata.domain.entity.EnrichTarget
import nrw.openbert.backend.parldata.domain.entity.OparlSystem
import nrw.openbert.backend.parldata.domain.entity.QueueTarget
import nrw.openbert.backend.parldata.domain.entity.ResolveTarget
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class QueueFailureRecorderTest {
    private val crawlTargetRepository = mock(CrawlTargetRepository::class.java)
    private val enrichTargetRepository = mock(EnrichTargetRepository::class.java)
    private val resolveTargetRepository = mock(ResolveTargetRepository::class.java)
    private val scopes = mock(IScopes::class.java)
    private val crawlRecorder = CrawlFailureRecorder(crawlTargetRepository, scopes)
    private val enrichRecorder = EnrichFailureRecorder(enrichTargetRepository, scopes)
    private val resolveRecorder = ResolveFailureRecorder(resolveTargetRepository, scopes)

    @Test
    fun `marks the crawl target as failed and captures the error in sentry`() {
        val target = crawlTarget()
        `when`(crawlTargetRepository.findById(target.id)).thenReturn(Optional.of(target))

        val error = RuntimeException("upstream returned 404")
        crawlRecorder.record(target.id, error)

        assertCaptured(error) { scope ->
            verify(scope).setTag("crawl.target.id", target.id.toString())
            verify(scope).setExtra("crawl.target.url", target.resource.url.value)
        }

        verify(crawlTargetRepository).save(target)
        assertFailed(target, error.message)
    }

    @Test
    fun `marks the enrich target as failed and captures the error in sentry`() {
        val target = EnrichTarget(rawEntityId = UUID.randomUUID(), entityType = OparlType.LOCATION)
        `when`(enrichTargetRepository.findById(target.id)).thenReturn(Optional.of(target))

        val error = RuntimeException("enrichment backend unavailable")
        enrichRecorder.record(target.id, error)

        assertCaptured(error) { scope ->
            verify(scope).setTag("enrich.target.id", target.id.toString())
            verify(scope).setExtra("enrich.target.rawEntityId", target.rawEntityId.toString())
            verify(scope).setExtra("enrich.target.entityType", OparlType.LOCATION.name)
        }

        verify(enrichTargetRepository).save(target)
        assertFailed(target, error.message)
    }

    @Test
    fun `marks the resolve target as failed and captures the error in sentry`() {
        val target = ResolveTarget(rawEntityId = UUID.randomUUID(), entityType = OparlType.PERSON)
        `when`(resolveTargetRepository.findById(target.id)).thenReturn(Optional.of(target))

        val error = RuntimeException("unresolved reference")
        resolveRecorder.record(target.id, error)

        assertCaptured(error) { scope ->
            verify(scope).setTag("resolve.target.id", target.id.toString())
            verify(scope).setExtra("resolve.target.rawEntityId", target.rawEntityId.toString())
            verify(scope).setExtra("resolve.target.entityType", OparlType.PERSON.name)
        }

        verify(resolveTargetRepository).save(target)
        assertFailed(target, error.message)
    }

    @Test
    fun `falls back to the exception class name when the error has no message`() {
        val target = crawlTarget()
        `when`(crawlTargetRepository.findById(target.id)).thenReturn(Optional.of(target))

        crawlRecorder.record(target.id, RuntimeException())

        assertEquals(RuntimeException::class.java.simpleName, target.failureReason)
    }

    private fun assertCaptured(
        error: Exception,
        verifyScope: (IScope) -> Unit,
    ) {
        val exceptionCaptor = ArgumentCaptor.forClass(Throwable::class.java)
        val callbackCaptor = ArgumentCaptor.forClass(ScopeCallback::class.java)
        verify(scopes).captureException(exceptionCaptor.capture(), callbackCaptor.capture())
        assertEquals(error, exceptionCaptor.value)

        val scope = mock(IScope::class.java)
        callbackCaptor.value.run(scope)
        verifyScope(scope)
    }

    private fun assertFailed(
        target: QueueTarget,
        expectedReason: String?,
    ) {
        assertNotNull(target.failedAt)
        assertEquals(expectedReason, target.failureReason)
    }

    private fun crawlTarget() =
        CrawlTarget.create(
            OparlResource(
                OparlSystem(OparlUrl("https://oparl.example.org/oparl/v1.1/system")),
                OparlUrl("https://oparl.example.org/oparl/v1.1/body/1"),
            ),
        )
}
