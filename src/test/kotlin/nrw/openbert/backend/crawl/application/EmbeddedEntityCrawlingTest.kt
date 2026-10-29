package nrw.openbert.backend.crawl.application

import jakarta.persistence.EntityManager
import nrw.openbert.backend.TestcontainersConfiguration
import nrw.openbert.backend.parldata.adapter.inbound.crawler.Crawler
import nrw.openbert.backend.parldata.adapter.inbound.crawler.processor.PersonProcessor
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.OparlClient
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlLocationDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlMembershipDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlPersonDto
import nrw.openbert.backend.parldata.application.port.outbound.repository.CrawlTargetRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.EnrichTargetRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawLocationRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawMembershipRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawPersonRepository
import nrw.openbert.backend.parldata.domain.entity.CrawlTarget
import nrw.openbert.backend.parldata.domain.entity.OparlSystem
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.bean.override.mockito.MockitoBean
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
class EmbeddedEntityCrawlingTest(
    @Autowired private val entityManager: EntityManager,
    @Autowired private val crawler: Crawler,
    @Autowired private val personProcessor: PersonProcessor,
    @Autowired private val crawlTargets: CrawlTargetRepository,
    @Autowired private val enrichTargets: EnrichTargetRepository,
    @Autowired private val persons: RawPersonRepository,
    @Autowired private val memberships: RawMembershipRepository,
    @Autowired private val locations: RawLocationRepository,
) {
    @MockitoBean
    private lateinit var oparlClient: OparlClient

    @Test
    fun `person list seeds new people and their own URL fills missing fields`() {
        val personResource = resource("person/new-from-list")
        val completeResource = resource("person/complete")
        val completedAt = crawlTargets.findByResource(completeResource)!!.lastCrawledAt
        val listTarget =
            crawlTargets.saveAndFlush(
                CrawlTarget.create(resource("body/123/person"), OparlType.PERSON).increaseReferenceCounter(),
            )
        `when`(oparlClient.get(listTarget.resource.url.value)).thenReturn(
            OparlListDto(
                data =
                    listOf(
                        OparlPersonDto(id = personResource.url.value, name = "List name"),
                        OparlPersonDto(id = completeResource.url.value, name = "Partial name"),
                    ),
            ),
        )

        crawler.run(listTarget.id)
        entityManager.flush()
        entityManager.clear()

        val seed = persons.findByResource(personResource)!!
        assertEquals("List name", seed.name)
        assertTrue(seed.email.isEmpty())
        val personTarget = assertDue(personResource, OparlType.PERSON)
        assertNotNull(crawlTargets.findById(listTarget.id).orElseThrow().lastCrawledAt)
        val completePerson = persons.findByResource(completeResource)!!
        assertEquals("Complete person", completePerson.name)
        assertEquals("Example", completePerson.familyName)
        assertEquals(listOf("person@example.org"), completePerson.email)
        assertEquals(listOf("+49 123 456"), completePerson.phone)
        assertEquals(completedAt, crawlTargets.findByResource(completeResource)!!.lastCrawledAt)
        assertNull(enrichTargets.findByRawEntityIdAndEntityType(completePerson.id, OparlType.PERSON))

        `when`(oparlClient.get(personResource.url.value)).thenReturn(
            OparlPersonDto(
                id = personResource.url.value,
                body = resource("body/123").url.value,
                name = "Full name",
                familyName = "Example",
                email = listOf("full@example.org"),
                phone = listOf("+49 555"),
            ),
        )
        crawler.run(personTarget.id)
        entityManager.flush()
        entityManager.clear()

        val fullPerson = persons.findByResource(personResource)!!
        assertEquals(seed.id, fullPerson.id)
        assertEquals("Full name", fullPerson.name)
        assertEquals("Example", fullPerson.familyName)
        assertEquals(resource("body/123").url, fullPerson.bodyUrl)
        assertEquals(listOf("full@example.org"), fullPerson.email)
        assertEquals(listOf("+49 555"), fullPerson.phone)
        val crawledAt = crawlTargets.findById(personTarget.id).orElseThrow().lastCrawledAt
        assertNotNull(crawledAt)
        verify(oparlClient).get(personResource.url.value)

        // A later page containing the same partial representation cannot erase full data.
        crawler.run(listTarget.id)
        entityManager.flush()
        entityManager.clear()
        assertEquals(listOf("full@example.org"), persons.findByResource(personResource)!!.email)
        assertEquals("Full name", persons.findByResource(personResource)!!.name)
        assertEquals(crawledAt, crawlTargets.findById(personTarget.id).orElseThrow().lastCrawledAt)
    }

    @Test
    fun `repeated list data neither updates a seed nor completes its pending crawl`() {
        val personResource = resource("person/list-seed")
        val target = assertDue(personResource, OparlType.PERSON)
        val listTarget =
            crawlTargets.saveAndFlush(
                CrawlTarget.create(resource("body/123/person"), OparlType.PERSON).increaseReferenceCounter(),
            )
        `when`(oparlClient.get(listTarget.resource.url.value)).thenReturn(
            OparlListDto(
                data =
                    listOf(
                        OparlPersonDto(
                            id = personResource.url.value,
                            name = "Another list name",
                            email = listOf("partial@example.org"),
                        ),
                    ),
            ),
        )

        crawler.run(listTarget.id)
        entityManager.flush()
        entityManager.clear()

        val person = persons.findByResource(personResource)!!
        assertEquals("Initial list name", person.name)
        assertTrue(person.email.isEmpty())
        assertEquals(target.id, assertDue(personResource, OparlType.PERSON).id)
        assertEquals(1, crawlTargets.findByResource(personResource)!!.referenceCount)
        assertNull(enrichTargets.findByRawEntityIdAndEntityType(person.id, OparlType.PERSON))
    }

    @Test
    fun `embedded memberships and locations initialize parent links without changing existing entities`() {
        val parent = resource("person/complete")
        val member = resource("membership/embedded")
        val location = resource("location/embedded")
        val embeddedMembership = OparlMembershipDto(id = member.url.value, role = "Initial role")
        val embeddedLocation = OparlLocationDto(id = location.url.value, streetAddress = "Main Street 1")

        personProcessor.process(
            parent,
            OparlPersonDto(
                id = parent.url.value,
                membership = listOf(embeddedMembership),
                locationObject = embeddedLocation,
            ),
        )
        entityManager.flush()
        entityManager.clear()

        assertEquals(parent.url, memberships.findByResource(member)!!.personUrl)
        assertEquals(listOf(parent.url.value), locations.findByResource(location)!!.personUrls)
        val memberTarget = assertDue(member, OparlType.MEMBERSHIP)
        assertDue(location, OparlType.LOCATION)
        `when`(oparlClient.get(member.url.value)).thenReturn(
            OparlMembershipDto(
                id = member.url.value,
                person = parent.url.value,
                organization = resource("organization/1").url.value,
                role = "Complete role",
                votingRight = true,
            ),
        )
        crawler.run(memberTarget.id)
        // Compare persisted timestamps, since PostgreSQL stores only microsecond precision.
        entityManager.flush()
        entityManager.clear()
        val completedAt = crawlTargets.findByResource(member)!!.lastCrawledAt
        assertNotNull(completedAt)

        // Even inferred back references must not mutate existing embedded entities.
        personProcessor.process(
            resource("person/another"),
            OparlPersonDto(
                membership = listOf(embeddedMembership),
                locationObject = embeddedLocation.copy(streetAddress = "Incomplete address"),
            ),
        )
        entityManager.flush()
        entityManager.clear()

        val fullMembership = memberships.findByResource(member)!!
        assertEquals(parent.url, fullMembership.personUrl)
        assertEquals("Complete role", fullMembership.role)
        assertEquals(true, fullMembership.votingRight)
        assertEquals(completedAt, crawlTargets.findByResource(member)!!.lastCrawledAt)
        val initialLocation = locations.findByResource(location)!!
        assertEquals(listOf(parent.url.value), initialLocation.personUrls)
        assertEquals("Main Street 1", initialLocation.streetAddress)
        assertDue(location, OparlType.LOCATION)
    }

    private fun assertDue(
        resource: OparlResource,
        type: OparlType,
    ): CrawlTarget {
        val target = assertNotNull(crawlTargets.findByResource(resource))
        assertNull(target.lastCrawledAt)
        assertEquals(type, target.entityType)
        assertTrue(target.referenceCount > 0)
        assertTrue(crawlTargets.findDue(Instant.now(), 100).any { it.id == target.id })
        return target
    }

    private fun resource(path: String): OparlResource {
        val system =
            entityManager.find(
                OparlSystem::class.java,
                UUID.fromString("9352fa21-68c6-4f4e-8832-08a0454ff179"),
            )
        return OparlResource(system, OparlUrl("https://oparl-demo.example.org/oparl/v1.1/$path"))
    }
}
