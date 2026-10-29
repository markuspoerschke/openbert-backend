package nrw.openbert.backend.parldata.adapter.inbound.scheduler

import io.sentry.IScope
import io.sentry.IScopes
import io.sentry.ScopeCallback
import nrw.openbert.backend.parldata.application.port.outbound.repository.CrawlTargetRepository
import nrw.openbert.backend.parldata.domain.entity.CrawlTarget
import nrw.openbert.backend.parldata.domain.entity.OparlSystem
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
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

class CrawlFailureRecorderTest {
    private val crawlTargetRepository = mock(CrawlTargetRepository::class.java)
    private val scopes = mock(IScopes::class.java)
    private val recorder = CrawlFailureRecorder(crawlTargetRepository, scopes)

    @Test
    fun `marks the crawl target as failed and captures the error in sentry`() {
        val target = crawlTarget()
        `when`(crawlTargetRepository.findById(target.id)).thenReturn(Optional.of(target))

        val error = RuntimeException("upstream returned 404")
        recorder.record(target.id, error)

        val exceptionCaptor = ArgumentCaptor.forClass(Throwable::class.java)
        val callbackCaptor = ArgumentCaptor.forClass(ScopeCallback::class.java)
        verify(scopes).captureException(exceptionCaptor.capture(), callbackCaptor.capture())
        assertEquals(error, exceptionCaptor.value)

        val scope = mock(IScope::class.java)
        callbackCaptor.value.run(scope)
        verify(scope).setTag("crawl.target.id", target.id.toString())
        verify(scope).setExtra("crawl.target.url", target.resource.url.value)

        verify(crawlTargetRepository).save(target)
        assertNotNull(target.failedAt)
        assertEquals(error.message, target.failureReason)
    }

    @Test
    fun `falls back to the exception class name when the error has no message`() {
        val target = crawlTarget()
        `when`(crawlTargetRepository.findById(target.id)).thenReturn(Optional.of(target))

        recorder.record(target.id, RuntimeException())

        assertEquals(RuntimeException::class.java.simpleName, target.failureReason)
    }

    private fun crawlTarget() =
        CrawlTarget.create(
            OparlResource(
                OparlSystem(OparlUrl("https://oparl.example.org/oparl/v1.1/system")),
                OparlUrl("https://oparl.example.org/oparl/v1.1/body/1"),
            ),
        )
}
