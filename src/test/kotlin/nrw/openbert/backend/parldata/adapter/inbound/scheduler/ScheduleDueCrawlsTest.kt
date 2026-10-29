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
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`
import java.time.Instant

class ScheduleDueCrawlsTest {
    private val crawlTargetRepository = mock(CrawlTargetRepository::class.java)
    private val crawler = mock(Crawler::class.java)
    private val scheduleDueCrawls = ScheduleDueCrawls(crawlTargetRepository, crawler)

    @Test
    fun `crawls due targets directly instead of enqueueing them`() {
        val target = crawlTarget()
        `when`(crawlTargetRepository.findDue(anyInstant(), anyInt())).thenReturn(listOf(target))

        scheduleDueCrawls.run()

        verify(crawler).run(target.id)
        verifyNoMoreInteractions(crawler)
    }

    @Test
    fun `does nothing when no targets are due`() {
        `when`(crawlTargetRepository.findDue(anyInstant(), anyInt())).thenReturn(emptyList())

        scheduleDueCrawls.run()

        verifyNoMoreInteractions(crawler)
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
