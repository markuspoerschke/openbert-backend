package nrw.openbert.backend.enrich.application

import jakarta.persistence.EntityManager
import nrw.openbert.backend.TestcontainersConfiguration
import nrw.openbert.backend.parldata.application.enrich.Enricher
import nrw.openbert.backend.parldata.application.port.outbound.repository.EnrichTargetRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawLocationRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolveTargetRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedLocationRepository
import nrw.openbert.backend.parldata.application.resolve.Resolver
import nrw.openbert.backend.parldata.domain.enums.OparlType
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.core.io.ClassPathResource
import org.springframework.data.domain.PageRequest
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID
import javax.sql.DataSource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@Transactional
class LocationEnrichmentMigrationTest(
    @Autowired private val dataSource: DataSource,
    @Autowired private val entityManager: EntityManager,
    @Autowired private val targetRepository: EnrichTargetRepository,
    @Autowired private val rawLocationRepository: RawLocationRepository,
    @Autowired private val resolveTargetRepository: ResolveTargetRepository,
    @Autowired private val resolvedLocationRepository: ResolvedLocationRepository,
    @Autowired private val enricher: Enricher,
    @Autowired private val resolver: Resolver,
) {
    private val existingTargetId = UUID.fromString("40000000-0000-4000-8000-000000000002")

    @ParameterizedTest
    @ValueSource(strings = ["V11__enqueue_existing_locations_for_enrichment.sql", "V13__reenrich_location_addresses.sql"])
    fun `queues every existing location and updates resolved values through enrichment`(migration: String) {
        val rawIds = rawLocationRepository.findAll().map { it.id }.toSet()
        val existingTarget = targetRepository.findById(existingTargetId).orElseThrow()

        migrate(migration)

        val dueTargets = targetRepository.findDue(Instant.now(), PageRequest.of(0, 100))
        assertEquals(rawIds, dueTargets.map { it.rawEntityId }.toSet())
        dueTargets.forEach {
            assertEquals(OparlType.LOCATION, it.entityType)
            assertEquals(1, it.referenceCount)
            assertNull(it.jobId)
            assertNull(it.scheduledAt)
        }
        val requeued = targetRepository.findById(existingTargetId).orElseThrow()
        assertEquals(existingTarget.rawEntityId, requeued.rawEntityId)
        assertEquals(existingTarget.createdAt, requeued.createdAt)
        assertEquals(existingTarget.lastEnrichedAt, requeued.lastEnrichedAt)
        val unrelatedTarget = targetRepository.findById(
            UUID.fromString("40000000-0000-4000-8000-000000000001"),
        ).orElseThrow()
        assertEquals(0, unrelatedTarget.referenceCount)

        enricher.run(requeued.id)
        val resolveTarget = resolveTargetRepository
            .findByRawEntityIdAndEntityType(requeued.rawEntityId, OparlType.LOCATION)
        assertNotNull(resolveTarget)
        resolver.run(resolveTarget.id)
        entityManager.flush()
        entityManager.clear()

        val raw = rawLocationRepository.findById(requeued.rawEntityId).orElseThrow()
        val resolved = resolvedLocationRepository.findByResource(raw.resource)
        assertNotNull(resolved)
        assertEquals("Rathausplatz 1", resolved.streetAddress)
        assertEquals("12345", resolved.postalCode)
        assertEquals("  Rathausplatz 1  ", raw.streetAddress)
    }

    @ParameterizedTest
    @ValueSource(strings = ["V11__enqueue_existing_locations_for_enrichment.sql", "V13__reenrich_location_addresses.sql"])
    fun `clears old scheduling markers so previously scheduled locations become due again`(migration: String) {
        val existingTarget = targetRepository.findById(existingTargetId).orElseThrow()
        existingTarget.enqueue().markAsScheduled(UUID.randomUUID())
        existingTarget.scheduledAt = Instant.now().plusSeconds(3600)

        migrate(migration)

        val requeued = targetRepository.findById(existingTargetId).orElseThrow()
        assertEquals(2, requeued.referenceCount)
        assertNull(requeued.jobId)
        assertNull(requeued.scheduledAt)
        assertEquals(existingTarget.createdAt, requeued.createdAt)
        assertEquals(existingTarget.lastEnrichedAt, requeued.lastEnrichedAt)
        val dueIds = targetRepository.findDue(Instant.now(), PageRequest.of(0, 100)).map { it.id }
        assertTrue(existingTargetId in dueIds)
    }

    private fun migrate(migration: String) {
        entityManager.flush()
        ResourceDatabasePopulator(
            ClassPathResource("db/migration/$migration"),
        ).execute(dataSource)
        entityManager.clear()
    }
}
