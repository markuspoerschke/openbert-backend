package nrw.openbert.backend.crawl.application

import nrw.openbert.backend.parldata.application.crawl.CrawlTargetEnqueuer
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.domain.entity.CrawlTarget
import nrw.openbert.backend.parldata.application.port.outbound.repository.CrawlTargetRepository
import nrw.openbert.backend.parldata.domain.entity.OparlSystem
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
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
        assertNull(target.lastIndexedAt)
        assertNull(target.lastCrawledAt)
    }

    @Test
    fun `queues a newly indexed resource for its own crawl`() {
        val resource = resource("https://oparl.example.org/person/1")

        enqueuer.markAsIndexed(resource, OparlType.PERSON)

        val target = captureSavedTarget()
        assertEquals(resource, target.resource)
        assertEquals(OparlType.PERSON, target.entityType)
        assertNotNull(target.lastIndexedAt)
        assertNull(target.lastCrawledAt)
        assertEquals(1, target.referenceCount)
    }

    @Test
    fun `indexing an already crawled resource makes it due again`() {
        val resource = resource("https://oparl.example.org/person/1")
        val target = CrawlTarget(resource = resource).markAsCrawled()
        `when`(repository.findByResource(resource)).thenReturn(target)

        enqueuer.markAsIndexed(resource, OparlType.PERSON)

        assertNotNull(target.lastIndexedAt)
        assertNull(target.lastCrawledAt)
        assertEquals(1, target.referenceCount)
        assertEquals(OparlType.PERSON, target.entityType)
        verify(repository).save(target)
    }

    @Test
    fun `indexing preserves an already scheduled resource crawl`() {
        val resource = resource("https://oparl.example.org/location/1")
        val target = CrawlTarget(
            resource = resource,
            referenceCount = 4,
            scheduledAt = Instant.parse("2026-07-31T08:00:00Z"),
            jobId = UUID.randomUUID(),
        )
        `when`(repository.findByResource(resource))
            .thenReturn(target)

        val scheduledAt = target.scheduledAt
        val jobId = target.jobId

        enqueuer.markAsIndexed(resource, OparlType.LOCATION)

        assertNotNull(target.lastIndexedAt)
        assertNull(target.lastCrawledAt)
        assertEquals(5, target.referenceCount)
        assertEquals(scheduledAt, target.scheduledAt)
        assertEquals(jobId, target.jobId)
        verify(repository).save(target)
    }

    private fun resource(url: String) =
        OparlResource(system, OparlUrl(url))

    private fun captureSavedTarget(): CrawlTarget {
        val captor = ArgumentCaptor.forClass(CrawlTarget::class.java)
        verify(repository).save(captor.capture())
        return captor.value
    }
}
