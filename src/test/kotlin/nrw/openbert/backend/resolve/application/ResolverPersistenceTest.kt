package nrw.openbert.backend.resolve.application

import jakarta.persistence.EntityManager
import nrw.openbert.backend.TestcontainersConfiguration
import nrw.openbert.backend.oparl.domain.entity.OparlSystem
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawBody
import nrw.openbert.backend.oparl.domain.entity.raw.RawSystem
import nrw.openbert.backend.oparl.domain.repository.raw.RawBodyRepository
import nrw.openbert.backend.resolve.domain.entity.ResolveTarget
import nrw.openbert.backend.resolve.domain.entity.ResolveEntityType
import nrw.openbert.backend.resolve.domain.repository.ResolveTargetRepository
import nrw.openbert.backend.oparl.domain.repository.raw.RawOrganizationRepository
import nrw.openbert.backend.oparl.domain.repository.raw.RawPaperRepository
import nrw.openbert.backend.oparl.domain.repository.raw.RawSystemRepository
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedBodyRepository
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedOrganizationRepository
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedSystemRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@Transactional
class ResolverPersistenceTest(
    @Autowired private val resolver: Resolver,
    @Autowired private val targetRepository: ResolveTargetRepository,
    @Autowired private val entityManager: EntityManager,
    @Autowired private val rawSystemRepository: RawSystemRepository,
    @Autowired private val rawBodyRepository: RawBodyRepository,
    @Autowired private val rawOrganizationRepository: RawOrganizationRepository,
    @Autowired private val rawPaperRepository: RawPaperRepository,
    @Autowired private val resolvedSystemRepository: ResolvedSystemRepository,
    @Autowired private val resolvedBodyRepository: ResolvedBodyRepository,
    @Autowired private val resolvedOrganizationRepository: ResolvedOrganizationRepository,
) {
    @Test
    fun `resolves a system`() {
        val raw = rawSystemRepository.save(
            RawSystem(resource("system/source")).apply {
                name = "Source system"
                oparlVersion = "https://schema.oparl.org/1.1/"
            },
        )
        val target = targetRepository.save(ResolveTarget.create(raw).enqueue())

        resolver.run(target.id)

        val resolved = resolvedSystemRepository.findByResource(raw.resource)
        assertNotNull(resolved)
        assertEquals("Source system", resolved.name)
        assertEquals(raw.resource, resolved.resource)
        assertResolved(target.id)
    }

    @Test
    fun `resolves a body`() {
        val raw = rawBodyRepository.save(
            RawBody(resource("body/source")).apply { name = "Source body" },
        )
        val target = targetRepository.save(ResolveTarget.create(raw).enqueue())

        resolver.run(target.id)

        val resolved = resolvedBodyRepository.findByResource(raw.resource)
        assertNotNull(resolved)
        assertEquals("Source body", resolved.name)
        assertEquals(raw.resource, resolved.resource)
        assertResolved(target.id)
    }

    @Test
    fun `resolves an organization with its real body relation`() {
        val raw = rawOrganizationRepository
            .findById(UUID.fromString("10000000-0000-4000-8000-000000000002"))
            .orElseThrow()
        val target = targetRepository.save(ResolveTarget.create(raw).enqueue())

        resolver.run(target.id)

        val resolved = resolvedOrganizationRepository.findByResource(raw.resource)
        assertNotNull(resolved)
        assertEquals(UUID.fromString("903c26aa-dc8f-4d34-afd6-afdc5040dbd3"), resolved.body.id)
        assertEquals(raw.resource, resolved.resource)
        assertResolved(target.id)
    }

    @Test
    fun `exposes every entity type to the scheduler`() {
        assertEquals(ResolveEntityType.entries.toSet(), resolver.supportedEntityTypes)
    }

    @Test
    fun `all supported entity types become due`() {
        val rawSystem = rawSystemRepository.save(RawSystem(resource("system/due")))
        val rawPaper = rawPaperRepository
            .findById(UUID.fromString("10000000-0000-4000-8000-000000000003"))
            .orElseThrow()
        val systemTarget = targetRepository.save(ResolveTarget.create(rawSystem).enqueue())
        val paperTarget = targetRepository.save(ResolveTarget.create(rawPaper).enqueue())

        val dueTargets = targetRepository.findDue(
            Instant.now(),
            resolver.supportedEntityTypes,
            PageRequest.of(0, 3),
        )

        assertEquals(setOf(systemTarget.id, paperTarget.id), dueTargets.map { it.id }.toSet())
    }

    private fun assertResolved(targetId: UUID) {
        val target = targetRepository.findById(targetId).orElseThrow()
        assertNotNull(targetRepository.findById(target.id).orElseThrow().lastResolvedAt)
        assertEquals(0, targetRepository.findById(target.id).orElseThrow().referenceCount)
    }

    private fun resource(path: String): OparlResource {
        val system = entityManager.find(
            OparlSystem::class.java,
            UUID.fromString("9352fa21-68c6-4f4e-8832-08a0454ff179"),
        )
        return OparlResource(system, OparlUrl("https://oparl-demo.example.org/oparl/v1.1/$path"))
    }
}
