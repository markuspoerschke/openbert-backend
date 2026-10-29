package nrw.openbert.backend.parldata.application.resolve

import jakarta.persistence.EntityManager
import nrw.openbert.backend.TestcontainersConfiguration
import nrw.openbert.backend.parldata.application.port.outbound.repository.EnrichedLocationInterface
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawLocationRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolveTargetRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedLocationRepository
import nrw.openbert.backend.parldata.domain.enrich.enricher.LocationOverrides
import nrw.openbert.backend.parldata.domain.entity.ResolveTarget
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedLocation
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@Transactional
class LocationResolverPersistenceTest(
    @Autowired private val resolver: Resolver,
    @Autowired private val targetRepository: ResolveTargetRepository,
    @Autowired private val rawLocationRepository: RawLocationRepository,
    @Autowired private val enrichedLocationRepository: EnrichedLocationInterface,
    @Autowired private val resolvedLocationRepository: ResolvedLocationRepository,
    @Autowired private val entityManager: EntityManager,
) {
    private val rawId = UUID.fromString("10000000-0000-4000-8000-000000000007")

    @Test
    fun `applies enabled overrides and preserves raw values for disabled and other fields`() {
        val resolved = resolve()

        assertEquals("Enriched Street 42", resolved.streetAddress)
        assertEquals("12345", resolved.postalCode)
        assertNull(resolved.room)
        assertEquals("Enriched description", resolved.description)
        assertEquals("Enriched city", resolved.locality)
        assertEquals("Centre", resolved.subLocality)
        assertEquals("Point", resolved.geojson?.get("type"))
        assertEquals("CC0-1.0", resolved.license)
        assertEquals(listOf("rathaus"), resolved.keyword)
        assertEquals("https://example.org/location", resolved.web)
        assertEquals(UUID.fromString("903c26aa-dc8f-4d34-afd6-afdc5040dbd3"), resolved.body.id)
        assertRawValues()
    }

    @Test
    fun `updates an existing resolved location with explicit null overrides for every field`() {
        val originalId = resolve().id
        enrichedLocationRepository.findById(rawId).orElseThrow().applyValuesFrom(
            LocationOverrides(
                overrideStreetAddress = true,
                overridePostalCode = true,
                overrideRoom = true,
                overrideDescription = true,
                overrideLocality = true,
            ),
        )

        val resolved = resolve()

        assertEquals(originalId, resolved.id)
        assertNull(resolved.streetAddress)
        assertNull(resolved.postalCode)
        assertNull(resolved.room)
        assertNull(resolved.description)
        assertNull(resolved.locality)
        assertRawValues()
    }

    @Test
    fun `disabling overrides restores the latest raw values even when override values remain`() {
        val originalId = resolve().id
        enrichedLocationRepository.findById(rawId).orElseThrow().applyValuesFrom(
            LocationOverrides(
                streetAddress = "Ignored street",
                postalCode = "Ignored postal code",
                room = "Ignored room",
                description = "Ignored description",
                locality = "Ignored city",
            ),
        )
        rawLocationRepository.findById(rawId).orElseThrow().streetAddress = "Updated raw street"

        val resolved = resolve()

        assertEquals(originalId, resolved.id)
        assertEquals("Updated raw street", resolved.streetAddress)
        assertEquals("12345", resolved.postalCode)
        assertEquals("Raw room", resolved.room)
        assertEquals("Raw description", resolved.description)
        assertEquals("Raw city", resolved.locality)
    }

    @Test
    fun `resolves without enrichment and removes previously applied overrides`() {
        val originalId = resolve().id
        enrichedLocationRepository.deleteById(rawId)

        val resolved = resolve()

        assertEquals(originalId, resolved.id)
        assertEquals("Raw Street 1", resolved.streetAddress)
        assertEquals("12345", resolved.postalCode)
        assertEquals("Raw room", resolved.room)
        assertEquals("Raw description", resolved.description)
        assertEquals("Raw city", resolved.locality)
        assertRawValues()
    }

    private fun resolve(): ResolvedLocation {
        val raw = rawLocationRepository.findById(rawId).orElseThrow()
        val target =
            targetRepository.findByRawEntityIdAndEntityType(rawId, raw.getType())
                ?: ResolveTarget.create(raw)
        targetRepository.save(target.enqueue())

        resolver.run(target.id)
        entityManager.flush()
        entityManager.clear()

        return assertNotNull(resolvedLocationRepository.findByResource(raw.resource))
    }

    private fun assertRawValues() {
        val raw = rawLocationRepository.findById(rawId).orElseThrow()
        assertEquals("Raw Street 1", raw.streetAddress)
        assertEquals("12345", raw.postalCode)
        assertEquals("Raw room", raw.room)
        assertEquals("Raw description", raw.description)
        assertEquals("Raw city", raw.locality)
    }
}
