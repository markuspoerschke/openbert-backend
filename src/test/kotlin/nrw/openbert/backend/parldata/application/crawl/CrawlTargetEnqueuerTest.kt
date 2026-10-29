package nrw.openbert.backend.parldata.application.crawl

import nrw.openbert.backend.parldata.application.port.outbound.repository.CrawlTargetRepository
import nrw.openbert.backend.parldata.domain.entity.CrawlTarget
import nrw.openbert.backend.parldata.domain.entity.OparlSystem
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CrawlTargetEnqueuerTest {
    private val repository = mock(CrawlTargetRepository::class.java)
    private val enqueuer = CrawlTargetEnqueuer(repository)
    private val system = OparlSystem(OparlUrl("https://oparl.example.org/system"))

    @Test
    fun `creates a referenced crawl target with the actual URL value`() {
        val resource = resource("https://oparl.example.org/body/1")
        `when`(repository.findByResource(resource))
            .thenReturn(null)

        enqueuer.enqueue(resource, OparlType.BODY)

        val target = captureSavedTarget()
        assertEquals(resource, target.resource)
        assertEquals(OparlType.BODY, target.entityType)
        assertEquals(1, target.referenceCount)
        assertNull(target.lastCrawledAt)
    }

    @Test
    fun `marks a processed target as crawled`() {
        val resource = resource("https://oparl.example.org/location/1")
        val target =
            CrawlTarget(
                resource = resource,
                referenceCount = 4,
                scheduledAt = Instant.parse("2026-07-31T08:00:00Z"),
                jobId = UUID.randomUUID(),
            )
        `when`(repository.findByResource(resource))
            .thenReturn(target)

        enqueuer.markAsCrawled(resource)

        assertNotNull(target.lastCrawledAt)
        assertEquals(0, target.referenceCount)
        assertNull(target.scheduledAt)
        assertNull(target.jobId)
        verify(repository).save(target)
    }

    private fun resource(url: String) = OparlResource(system, OparlUrl(url))

    private fun captureSavedTarget(): CrawlTarget {
        val captor = ArgumentCaptor.forClass(CrawlTarget::class.java)
        verify(repository).save(captor.capture())
        return captor.value
    }
}
