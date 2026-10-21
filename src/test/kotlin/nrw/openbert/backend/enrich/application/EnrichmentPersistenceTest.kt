package nrw.openbert.backend.enrich.application

import jakarta.persistence.EntityManager
import nrw.openbert.backend.TestcontainersConfiguration
import nrw.openbert.backend.parldata.adapter.inbound.crawler.processor.LocationProcessor
import nrw.openbert.backend.parldata.adapter.inbound.crawler.processor.SystemProcessor
import nrw.openbert.backend.parldata.adapter.inbound.scheduler.ScheduleDueEnriches
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlLocationDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlSystemDto
import nrw.openbert.backend.parldata.application.enrich.Enricher
import nrw.openbert.backend.parldata.application.enrich.EnrichTargetEnqueuer
import nrw.openbert.backend.parldata.application.port.outbound.repository.CrawlTargetRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.EnrichedLocationInterface
import nrw.openbert.backend.parldata.application.port.outbound.repository.EnrichTargetRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawLegislativeTermRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawLocationRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolveTargetRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedLocationRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedSystemRepository
import nrw.openbert.backend.parldata.application.resolve.Resolver
import nrw.openbert.backend.parldata.domain.entity.EnrichTarget
import nrw.openbert.backend.parldata.domain.entity.OparlSystem
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.jobrunr.jobs.JobId
import org.jobrunr.jobs.lambdas.JobLambda
import org.jobrunr.scheduling.JobScheduler
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@Transactional
class EnrichmentPersistenceTest(
    @Autowired private val enricher: Enricher,
    @Autowired private val enqueuer: EnrichTargetEnqueuer,
    @Autowired private val resolver: Resolver,
    @Autowired private val locationProcessor: LocationProcessor,
    @Autowired private val systemProcessor: SystemProcessor,
    @Autowired private val targetRepository: EnrichTargetRepository,
    @Autowired private val resolveTargetRepository: ResolveTargetRepository,
    @Autowired private val crawlTargetRepository: CrawlTargetRepository,
    @Autowired private val rawLocationRepository: RawLocationRepository,
    @Autowired private val rawLegislativeTermRepository: RawLegislativeTermRepository,
    @Autowired private val enrichedLocationRepository: EnrichedLocationInterface,
    @Autowired private val resolvedLocationRepository: ResolvedLocationRepository,
    @Autowired private val resolvedSystemRepository: ResolvedSystemRepository,
    @Autowired private val entityManager: EntityManager,
) {
    @Test
    fun `crawling a location queues enrichment before resolution`() {
        val raw = locationProcessor.process(
            resource("location/enrichment"),
            OparlLocationDto(
                streetAddress = "  Rathausplatz 1  ",
                postalCode = "  12345  ",
                bodies = listOf("https://oparl-demo.example.org/oparl/v1.1/body/123"),
            ),
        )
        entityManager.flush()
        val target = targetRepository.findByRawEntityIdAndEntityType(raw.id, OparlType.LOCATION)
        assertNotNull(target)
        assertEquals(1, target.referenceCount)
        assertNull(resolveTargetRepository.findByRawEntityIdAndEntityType(raw.id, OparlType.LOCATION))
        assertNotNull(crawlTargetRepository.findByResource(raw.resource)?.lastCrawledAt)

        enricher.run(target.id)
        entityManager.flush()
        entityManager.clear()

        val enriched = enrichedLocationRepository.findById(raw.id).orElseThrow()
        assertEquals(raw.id, enriched.id)
        assertTrue(enriched.overrideStreetAddress)
        assertEquals("Rathausplatz 1", enriched.streetAddress)
        assertTrue(enriched.overridePostalCode)
        assertEquals("12345", enriched.postalCode)
        assertEquals("  Rathausplatz 1  ", rawLocationRepository.findById(raw.id).orElseThrow().streetAddress)
        assertCompleted(target.id)
        val resolveTarget = resolveTargetRepository.findByRawEntityIdAndEntityType(raw.id, OparlType.LOCATION)
        assertNotNull(resolveTarget)
        assertEquals(1, resolveTarget.referenceCount)

        resolver.run(resolveTarget.id)
        entityManager.flush()
        entityManager.clear()
        val resolved = resolvedLocationRepository.findByResource(raw.resource)
        assertNotNull(resolved)
        assertEquals("Rathausplatz 1", resolved.streetAddress)
        assertEquals("12345", resolved.postalCode)
    }

    @Test
    fun `a type without an enricher passes from crawling through enrichment to resolution`() {
        val raw = systemProcessor.process(
            resource("system/enrichment"),
            OparlSystemDto(name = "Source system", oparlVersion = "https://schema.oparl.org/1.1/"),
        )
        val target = targetRepository.findByRawEntityIdAndEntityType(raw.id, OparlType.SYSTEM)
        assertNotNull(target)
        assertNull(resolveTargetRepository.findByRawEntityIdAndEntityType(raw.id, OparlType.SYSTEM))

        enricher.run(target.id)

        val resolveTarget = resolveTargetRepository.findByRawEntityIdAndEntityType(raw.id, OparlType.SYSTEM)
        assertNotNull(resolveTarget)
        resolver.run(resolveTarget.id)
        entityManager.flush()
        assertEquals("Source system", resolvedSystemRepository.findByResource(raw.resource)?.name)
        assertCompleted(target.id)
    }

    @Test
    fun `re-enrichment updates an existing location and requeues resolution`() {
        val rawId = UUID.fromString("10000000-0000-4000-8000-000000000005")
        val raw = rawLocationRepository.findById(rawId).orElseThrow()
        val originalCreated = enrichedLocationRepository.findById(rawId).orElseThrow().created
        enqueuer.enqueue(raw)
        val target = targetRepository.findByRawEntityIdAndEntityType(rawId, OparlType.LOCATION)!!
        enricher.run(target.id)

        val resolveTarget = resolveTargetRepository.findByRawEntityIdAndEntityType(rawId, OparlType.LOCATION)!!
        resolver.run(resolveTarget.id)
        raw.postalCode = "  54321  "
        enqueuer.enqueue(raw)
        enricher.run(target.id)
        entityManager.flush()
        entityManager.clear()

        val enriched = enrichedLocationRepository.findById(rawId).orElseThrow()
        assertEquals(originalCreated, enriched.created)
        assertEquals("54321", enriched.postalCode)
        assertEquals(target.id, targetRepository.findByRawEntityIdAndEntityType(rawId, OparlType.LOCATION)?.id)
        assertEquals(1, resolveTargetRepository.findById(resolveTarget.id).orElseThrow().referenceCount)
        assertCompleted(target.id)

        resolver.run(resolveTarget.id)
        entityManager.flush()
        entityManager.clear()
        assertEquals("54321", resolvedLocationRepository.findByResource(raw.resource)?.postalCode)
    }

    @Test
    fun `another crawl requeues a completed enrichment target`() {
        val raw = rawLegislativeTermRepository
            .findById(UUID.fromString("10000000-0000-4000-8000-000000000001")).orElseThrow()

        enqueuer.enqueue(raw)
        entityManager.flush()
        entityManager.clear()

        val target = targetRepository.findByRawEntityIdAndEntityType(raw.id, OparlType.LEGISLATIVE_TERM)!!
        assertEquals(UUID.fromString("40000000-0000-4000-8000-000000000001"), target.id)
        assertEquals(1, target.referenceCount)
        assertTrue(dueTargets().any { it.id == target.id })
    }

    @Test
    fun `scheduler queues due enrichment once and excludes inactive future and scheduled targets`() {
        val due = targetRepository.save(target().enqueue())
        targetRepository.save(target())
        targetRepository.save(target().enqueue().apply { scheduledAt = Instant.now().plusSeconds(3600) })
        targetRepository.save(target().enqueue().markAsScheduled(UUID.randomUUID()))
        val jobScheduler = mock(JobScheduler::class.java)
        val jobId = UUID.randomUUID()
        `when`(jobScheduler.enqueue(any(JobLambda::class.java))).thenReturn(JobId(jobId))
        val scheduler = ScheduleDueEnriches(targetRepository, enricher, jobScheduler)

        assertEquals(listOf(due.id), dueTargets().map { it.id })
        scheduler.run()
        scheduler.run()
        entityManager.flush()
        entityManager.clear()

        verify(jobScheduler, times(1)).enqueue(any(JobLambda::class.java))
        val scheduled = targetRepository.findById(due.id).orElseThrow()
        assertEquals(jobId, scheduled.jobId)
        assertNotNull(scheduled.scheduledAt)
        assertTrue(dueTargets().isEmpty())
    }

    private fun dueTargets() = targetRepository.findDue(Instant.now(), PageRequest.of(0, 100))

    private fun target() = EnrichTarget(rawEntityId = UUID.randomUUID(), entityType = OparlType.PERSON)

    private fun assertCompleted(targetId: UUID) {
        val target = targetRepository.findById(targetId).orElseThrow()
        assertNotNull(target.lastEnrichedAt)
        assertEquals(0, target.referenceCount)
        assertNull(target.jobId)
        assertNull(target.scheduledAt)
    }

    private fun resource(path: String): OparlResource {
        val system = entityManager.find(OparlSystem::class.java, UUID.fromString("9352fa21-68c6-4f4e-8832-08a0454ff179"))
        return OparlResource(system, OparlUrl("https://oparl-demo.example.org/oparl/v1.1/$path"))
    }
}
