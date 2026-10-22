package nrw.openbert.backend.resolve.application

import jakarta.persistence.EntityManager
import nrw.openbert.backend.TestcontainersConfiguration
import nrw.openbert.backend.parldata.adapter.inbound.crawler.processor.MeetingProcessor
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlMeetingDto
import nrw.openbert.backend.parldata.application.enrich.Enricher
import nrw.openbert.backend.parldata.application.exception.UnresolvedRelationException
import nrw.openbert.backend.parldata.application.port.outbound.repository.EnrichTargetRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawMeetingRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolveTargetRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedMeetingRepository
import nrw.openbert.backend.parldata.application.resolve.Resolver
import nrw.openbert.backend.parldata.domain.entity.ResolveTarget
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawBody
import nrw.openbert.backend.parldata.domain.entity.raw.RawMeeting
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@Transactional
class MeetingResolverPersistenceTest(
    @Autowired private val resolver: Resolver,
    @Autowired private val targetRepository: ResolveTargetRepository,
    @Autowired private val rawMeetingRepository: RawMeetingRepository,
    @Autowired private val meetingRepository: ResolvedMeetingRepository,
    @Autowired private val bodyRepository: ResolvedBodyRepository,
    @Autowired private val entityManager: EntityManager,
    @Autowired private val meetingProcessor: MeetingProcessor,
    @Autowired private val enricher: Enricher,
    @Autowired private val enrichTargetRepository: EnrichTargetRepository,
) {
    @Test
    fun `resolves a recrawled meeting again using its changed values and organizations`() {
        val raw = rawMeeting()
        val organizationBaseUrl = "https://oparl-demo.example.org/oparl/v1.1/organization"
        val originalDto = OparlMeetingDto(
            body = raw.bodyUrl!!.value,
            name = "Geplante Sitzung",
            meetingState = "terminiert",
            organization = listOf("$organizationBaseUrl/34"),
        )
        val firstCrawl = meetingProcessor.process(raw.resource, originalDto)
        val enrichTarget = assertNotNull(
            enrichTargetRepository.findByRawEntityIdAndEntityType(raw.id, OparlType.MEETING),
        )
        enricher.run(enrichTarget.id)
        val firstTarget = assertNotNull(targetRepository.findByRawEntityIdAndEntityType(raw.id, OparlType.MEETING))
        resolver.run(firstTarget.id)
        entityManager.flush()
        entityManager.clear()
        val existingId = assertNotNull(meetingRepository.findByResource(raw.resource)).id

        val updatedDto = originalDto.copy(
            name = "Durchgeführte Sitzung",
            meetingState = "durchgeführt",
            organization = listOf("$organizationBaseUrl/unresolved", "$organizationBaseUrl/41"),
        )
        val secondCrawl = meetingProcessor.process(raw.resource, updatedDto)
        enricher.run(enrichTarget.id)
        val nextTarget = assertNotNull(targetRepository.findByRawEntityIdAndEntityType(raw.id, OparlType.MEETING))
        assertEquals(firstCrawl.id, secondCrawl.id)
        assertEquals(firstTarget.id, nextTarget.id)
        assertEquals(1, nextTarget.referenceCount)
        assertNotNull(nextTarget.lastResolvedAt)

        resolver.run(nextTarget.id)
        entityManager.flush()
        entityManager.clear()

        val resolved = assertNotNull(meetingRepository.findByResource(raw.resource))
        assertEquals(existingId, resolved.id)
        assertEquals("Durchgeführte Sitzung", resolved.name)
        assertEquals("durchgeführt", resolved.meetingState)
        assertEquals(updatedDto.organization, resolved.organizations.map { it.resource.url.value })
        assertEquals(0, targetRepository.findById(nextTarget.id).orElseThrow().referenceCount)
    }

    @Test
    fun `resolves a meeting with an explicit body and no organizations or participants`() {
        val raw = rawMeeting()

        val target = resolve(raw)
        entityManager.flush()
        entityManager.clear()

        val resolved = assertNotNull(meetingRepository.findByResource(raw.resource))
        assertEquals(UUID.fromString("903c26aa-dc8f-4d34-afd6-afdc5040dbd3"), resolved.body.id)
        assertEquals(raw.name, resolved.name)
        assertTrue(resolved.organizations.isEmpty())
        assertNotNull(targetRepository.findById(target.id).orElseThrow().lastResolvedAt)
    }

    @Test
    fun `resolves a referenced raw body before resolving the meeting`() {
        val raw = rawMeeting().apply {
            bodyUrl = OparlUrl("https://oparl-demo.example.org/oparl/v1.1/body/meeting-source")
        }

        resolve(raw)
        entityManager.flush()
        entityManager.clear()

        val body = assertNotNull(bodyRepository.findByResource(OparlResource(raw.resource.system, raw.bodyUrl!!)))
        val resolved = assertNotNull(meetingRepository.findByResource(raw.resource))
        assertEquals(body.id, resolved.body.id)
        assertEquals("Körperschaft für Sitzungsauflösung", body.name)
    }

    @Test
    fun `refreshes the existing body from raw data on every meeting resolution`() {
        val bodyId = UUID.fromString("10000000-0000-4000-8000-000000000008")
        val rawBody = entityManager.find(RawBody::class.java, bodyId)
        val raw = rawMeeting().apply { bodyUrl = rawBody.resource.url }
        resolve(raw)
        entityManager.flush()
        entityManager.clear()
        val resolvedBodyId = assertNotNull(meetingRepository.findByResource(raw.resource)).body.id

        entityManager.find(RawBody::class.java, bodyId).name = "Aktualisierte Körperschaft"
        resolve(rawMeeting())
        entityManager.flush()
        entityManager.clear()

        val updated = assertNotNull(meetingRepository.findByResource(raw.resource)).body
        assertEquals(resolvedBodyId, updated.id)
        assertEquals("Aktualisierte Körperschaft", updated.name)

        // The known body must also refresh when the next crawl omits the body reference.
        entityManager.find(RawBody::class.java, bodyId).name = "Erneut aktualisierte Körperschaft"
        val incompleteRawMeeting = rawMeeting().apply { bodyUrl = null }
        resolve(incompleteRawMeeting)
        entityManager.flush()
        entityManager.clear()

        val refreshedFallback = assertNotNull(meetingRepository.findByResource(raw.resource)).body
        assertEquals(resolvedBodyId, refreshedFallback.id)
        assertEquals("Erneut aktualisierte Körperschaft", refreshedFallback.name)
    }

    @Test
    fun `infers the body from participants when the extension is absent`() {
        val raw = rawMeeting().apply {
            bodyUrl = null
            participantUrls.add("https://oparl-demo.example.org/oparl/v1.1/person/29")
        }

        resolve(raw)

        val resolved = assertNotNull(meetingRepository.findByResource(raw.resource))
        assertEquals(UUID.fromString("903c26aa-dc8f-4d34-afd6-afdc5040dbd3"), resolved.body.id)
    }

    @Test
    fun `keeps the existing body when a later crawl omits all body relations`() {
        val raw = rawMeeting()
        raw.organizationUrls.add("https://oparl-demo.example.org/oparl/v1.1/organization/34")
        resolve(raw)
        entityManager.flush()
        entityManager.clear()
        val existingId = assertNotNull(meetingRepository.findByResource(raw.resource)).id
        val incompleteRawMeeting = rawMeeting().apply {
            bodyUrl = null
            organizationUrls.clear()
            name = "Aktualisierte Sitzung"
        }

        resolve(incompleteRawMeeting)
        entityManager.flush()
        entityManager.clear()

        val resolved = assertNotNull(meetingRepository.findByResource(raw.resource))
        assertEquals(existingId, resolved.id)
        assertEquals(UUID.fromString("903c26aa-dc8f-4d34-afd6-afdc5040dbd3"), resolved.body.id)
        assertEquals("Aktualisierte Sitzung", resolved.name)
        assertTrue(resolved.organizations.isEmpty())
    }

    @Test
    fun `rejects a change to the body of an existing meeting`() {
        val raw = rawMeeting()
        resolve(raw)
        raw.bodyUrl = OparlUrl("https://oparl-demo.example.org/oparl/v1.1/body/456")

        val error = assertFailsWith<UnresolvedRelationException> { resolve(raw) }

        assertEquals("Conflicting body relation on ${raw.resource.url.value}", error.message)
        val resolved = assertNotNull(meetingRepository.findByResource(raw.resource))
        assertEquals(UUID.fromString("903c26aa-dc8f-4d34-afd6-afdc5040dbd3"), resolved.body.id)
    }

    @Test
    fun `rejects a body that conflicts with an organization`() {
        val raw = rawMeeting().apply {
            bodyUrl = OparlUrl("https://oparl-demo.example.org/oparl/v1.1/body/456")
            organizationUrls.add("https://oparl-demo.example.org/oparl/v1.1/organization/34")
        }

        val error = assertFailsWith<UnresolvedRelationException> { resolve(raw) }

        assertEquals("Conflicting body relations on ${raw.resource.url.value}", error.message)
        assertNull(meetingRepository.findByResource(raw.resource))
    }

    @Test
    fun `rejects a body that conflicts with a participant`() {
        val raw = rawMeeting().apply {
            bodyUrl = OparlUrl("https://oparl-demo.example.org/oparl/v1.1/body/456")
            participantUrls.add("https://oparl-demo.example.org/oparl/v1.1/person/29")
        }

        val error = assertFailsWith<UnresolvedRelationException> { resolve(raw) }

        assertEquals("Conflicting body relations on ${raw.resource.url.value}", error.message)
        assertNull(meetingRepository.findByResource(raw.resource))
    }

    @Test
    fun `still rejects a new meeting without any body relation`() {
        val raw = rawMeeting().apply { bodyUrl = null }

        val error = assertFailsWith<UnresolvedRelationException> { resolve(raw) }

        assertEquals("Missing body relation on ${raw.resource.url.value}", error.message)
        assertNull(meetingRepository.findByResource(raw.resource))
    }

    @Test
    fun `does not invent a body when the referenced body has not been crawled`() {
        val raw = rawMeeting().apply {
            bodyUrl = OparlUrl("https://oparl-demo.example.org/oparl/v1.1/body/not-crawled")
        }

        val error = assertFailsWith<UnresolvedRelationException> { resolve(raw) }

        assertEquals("Referenced body ${raw.bodyUrl!!.value} has not been crawled", error.message)
        assertNull(meetingRepository.findByResource(raw.resource))
    }

    private fun rawMeeting(): RawMeeting = rawMeetingRepository
        .findById(UUID.fromString("10000000-0000-4000-8000-000000000007"))
        .orElseThrow()

    private fun resolve(raw: RawMeeting): ResolveTarget {
        val candidate = ResolveTarget.create(raw)
        val target = targetRepository.save(
            (targetRepository.findByRawEntityIdAndEntityType(raw.id, candidate.entityType) ?: candidate).enqueue(),
        )
        resolver.run(target.id)
        return target
    }
}
