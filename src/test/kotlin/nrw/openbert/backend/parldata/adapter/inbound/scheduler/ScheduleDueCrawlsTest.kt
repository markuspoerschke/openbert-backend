package nrw.openbert.backend.parldata.adapter.inbound.scheduler

import nrw.openbert.backend.parldata.adapter.inbound.crawler.Crawler
import nrw.openbert.backend.parldata.application.port.outbound.repository.CrawlTargetRepository
import nrw.openbert.backend.parldata.domain.entity.CrawlTarget
import nrw.openbert.backend.parldata.domain.entity.OparlSystem
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`
import java.time.Instant

class ScheduleDueCrawlsTest {
    private val crawlTargetRepository = mock(CrawlTargetRepository::class.java)
    private val crawler = mock(Crawler::class.java)
    private val crawlFailureRecorder = mock(CrawlFailureRecorder::class.java)
    private val scheduleDueCrawls = ScheduleDueCrawls(crawlTargetRepository, crawler, crawlFailureRecorder)

    @Test
    fun `crawls due targets directly instead of enqueueing them`() {
        val target = crawlTarget()
        `when`(crawlTargetRepository.findDue(anyInstant(), anyInt())).thenReturn(listOf(target))

        scheduleDueCrawls.run()

        verify(crawler).run(target.id)
        verifyNoMoreInteractions(crawler, crawlFailureRecorder)
    }

    @Test
    fun `does nothing when no targets are due`() {
        `when`(crawlTargetRepository.findDue(anyInstant(), anyInt())).thenReturn(emptyList())

        scheduleDueCrawls.run()

        verifyNoMoreInteractions(crawler, crawlFailureRecorder)
    }

    @Test
    fun `records the failure instead of throwing when a crawl fails`() {
        val target = crawlTarget()
        `when`(crawlTargetRepository.findDue(anyInstant(), anyInt())).thenReturn(listOf(target))
        val error = RuntimeException("upstream returned 404")
        doThrow(error).`when`(crawler).run(target.id)

        scheduleDueCrawls.run()

        verify(crawlFailureRecorder).record(target.id, error)
        verifyNoMoreInteractions(crawlFailureRecorder)
    }

    private fun anyInstant(): Instant = any(Instant::class.java) ?: Instant.EPOCH

    private fun crawlTarget() =
        CrawlTarget.create(
            OparlResource(
                OparlSystem(OparlUrl("https://oparl.example.org/oparl/v1.1/system")),
                OparlUrl("https://oparl.example.org/oparl/v1.1/body/1"),
            ),
        )
}
