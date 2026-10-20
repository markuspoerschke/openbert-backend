package nrw.openbert.backend.resolve.application

import jakarta.persistence.EntityManager
import nrw.openbert.backend.TestcontainersConfiguration
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawLocationRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolveTargetRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedLocationRepository
import nrw.openbert.backend.parldata.application.resolve.Resolver
import nrw.openbert.backend.parldata.domain.entity.AbstractLocation
import nrw.openbert.backend.parldata.domain.entity.ResolveTarget
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@Transactional
class LocationEnrichmentPersistenceTest(
    @Autowired private val resolver: Resolver,
    @Autowired private val targetRepository: ResolveTargetRepository,
    @Autowired private val rawLocationRepository: RawLocationRepository,
    @Autowired private val resolvedLocationRepository: ResolvedLocationRepository,
    @Autowired private val entityManager: EntityManager,
) {
    @ParameterizedTest
    @CsvSource(
        "f8573c97-eac8-443f-bc5d-e61f73185777 | Nevigeser Straße 307 | 42553 | Velbert | NULL",
        "5f37b17f-74de-4940-b1ab-9f2dd7ab521a | NULL | NULL | NULL | NULL",
        "028a0258-262f-47cc-8e7f-95c97c4863da | NULL | NULL | 1, Am Lindenkamp 33 | Sitzungssaal",
        "b64c7df8-c0be-421b-8779-84fdcf93e080 | Donnerstraße 13 | 42555 | Velbert | Raum 41",
        "3c04fe2e-d8bc-4bf9-a9fc-caacbfa8b154 | NULL | NULL | " +
            "Den Sitzungsort entnehmen Sie bitte der Einladung bzw. dem Newseintrag | NULL",
        delimiter = '|',
        nullValues = ["NULL"],
    )
    fun `persists corrections while retaining raw data across repeated resolution`(
        rawId: String, street: String?, postalCode: String?, locality: String?, room: String?,
    ) {
        val raw = rawLocationRepository.findById(UUID.fromString(rawId)).orElseThrow()
        val originalValues = values(raw)
        val target = targetRepository.save(ResolveTarget.create(raw).enqueue())

        resolver.run(target.id)
        entityManager.flush()
        entityManager.clear()

        val resolved = assertNotNull(resolvedLocationRepository.findByResource(raw.resource))
        val resolvedId = resolved.id
        assertEquals(listOf(street, postalCode, locality, room), values(resolved))
        assertEquals(raw.description, resolved.description)
        assertEquals(UUID.fromString("903c26aa-dc8f-4d34-afd6-afdc5040dbd3"), resolved.body.id)
        assertEquals(originalValues, values(rawLocationRepository.findById(raw.id).orElseThrow()))
        assertNotNull(targetRepository.findById(target.id).orElseThrow().lastResolvedAt)

        targetRepository.save(targetRepository.findById(target.id).orElseThrow().enqueue())
        resolver.run(target.id)
        entityManager.flush()
        entityManager.clear()

        val resolvedAgain = assertNotNull(resolvedLocationRepository.findByResource(raw.resource))
        assertEquals(resolvedId, resolvedAgain.id)
        assertEquals(listOf(street, postalCode, locality, room), values(resolvedAgain))
        assertEquals(originalValues, values(rawLocationRepository.findById(raw.id).orElseThrow()))
    }

    @Test
    fun `enriches updated source values on an existing resolved location`() {
        val raw = rawLocationRepository.findById(UUID.fromString("f8573c97-eac8-443f-bc5d-e61f73185777")).orElseThrow()
        val target = targetRepository.save(ResolveTarget.create(raw).enqueue())
        resolver.run(target.id)
        val resolvedId = assertNotNull(resolvedLocationRepository.findByResource(raw.resource)).id

        raw.streetAddress = " Hauptstr.  4 "
        raw.room = " Ratssaal "
        rawLocationRepository.save(raw)
        targetRepository.save(targetRepository.findById(target.id).orElseThrow().enqueue())
        resolver.run(target.id)
        entityManager.flush()
        entityManager.clear()

        val resolved = assertNotNull(resolvedLocationRepository.findByResource(raw.resource))
        assertEquals(resolvedId, resolved.id)
        assertEquals(listOf("Hauptstraße 4", "42553", "Velbert", "Ratssaal"), values(resolved))
        assertEquals(" Hauptstr.  4 ", rawLocationRepository.findById(raw.id).orElseThrow().streetAddress)
    }

    private fun values(location: AbstractLocation): List<String?> =
        listOf(location.streetAddress, location.postalCode, location.locality, location.room)
}
