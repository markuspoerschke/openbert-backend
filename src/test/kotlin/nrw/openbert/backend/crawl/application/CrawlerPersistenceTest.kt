package nrw.openbert.backend.crawl.application

import jakarta.persistence.EntityManager
import nrw.openbert.backend.TestcontainersConfiguration
import nrw.openbert.backend.parldata.adapter.inbound.crawler.Crawler
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.OparlClient
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlAgendaItemDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlBodyDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlConsultationDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlFileDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlLegislativeTermDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlLocationDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlMeetingDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlMembershipDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlObjectDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlOrganizationDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlPaperDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlPersonDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlSystemDto
import nrw.openbert.backend.parldata.application.port.outbound.repository.CrawlTargetRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawMembershipRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawPersonRepository
import nrw.openbert.backend.parldata.domain.entity.CrawlTarget
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@Transactional
class CrawlerPersistenceTest(
    @Autowired private val crawler: Crawler,
    @Autowired private val targets: CrawlTargetRepository,
    @Autowired private val persons: RawPersonRepository,
    @Autowired private val memberships: RawMembershipRepository,
    @Autowired private val entityManager: EntityManager,
    @Autowired private val jdbc: JdbcTemplate,
) {
    @MockitoBean
    private lateinit var client: OparlClient

    @Test
    fun `every listed OParl type is stored and remains due until its resource is fetched`() {
        val list = listTarget()
        val objects = listedObjects()
        assertEquals(OparlType.entries.toSet(), objects.keys)
        respondWithList(list, objects.values.toList())

        crawler.run(list.id)
        entityManager.flush()

        assertNotNull(list.lastCrawledAt)
        val due = dueUrls()
        objects.forEach { (type, dto) ->
            val resource = resource(dto.id!!)
            val rawCount = jdbc.queryForObject(
                "SELECT count(*) FROM oparl_raw_${type.name.lowercase()} WHERE url = ?",
                Long::class.java,
                dto.id,
            )
            assertEquals(1L, rawCount)
            val target = assertNotNull(targets.findByResource(resource))
            assertEquals(type, target.entityType)
            assertNotNull(target.lastIndexedAt)
            assertNull(target.lastCrawledAt)
            assertTrue(resource.url.value in due)
            verify(client, never()).get(resource.url.value)

            `when`(client.get(resource.url.value)).thenReturn(dto)
            crawler.run(target.id)

            verify(client).get(resource.url.value)
            assertNotNull(target.lastCrawledAt)
            assertEquals(0, target.referenceCount)
        }
        assertTrue(objects.values.none { it.id in dueUrls() })
    }

    @Test
    fun `resource fetch fills missing person and inline membership fields after every list update`() {
        val list = listTarget()
        val personUrl = "$LIST_URL/person"
        val membershipUrl = "$LIST_URL/membership"
        val partial = OparlPersonDto(id = personUrl, name = "Example Person")
        val full = partial.copy(
            familyName = "Person",
            email = listOf("person@example.org"),
            membership = listOf(OparlMembershipDto(id = membershipUrl, person = personUrl)),
        )
        respondWithList(list, listOf(partial))
        `when`(client.get(personUrl)).thenReturn(full)
        `when`(client.get(membershipUrl)).thenReturn(
            OparlMembershipDto(id = membershipUrl, person = personUrl, role = "Chair"),
        )

        repeat(2) {
            crawler.run(list.id)
            assertNull(persons.findByResource(resource(personUrl))!!.familyName)
            assertTrue(personUrl in dueUrls())

            crawler.run(targets.findByResource(resource(personUrl))!!.id)

            val person = assertNotNull(persons.findByResource(resource(personUrl)))
            assertEquals("Person", person.familyName)
            assertEquals(listOf("person@example.org"), person.email)
            assertFalse(personUrl in dueUrls())
            assertTrue(membershipUrl in dueUrls())
            assertNull(memberships.findByResource(resource(membershipUrl))!!.role)

            crawler.run(targets.findByResource(resource(membershipUrl))!!.id)

            assertEquals("Chair", memberships.findByResource(resource(membershipUrl))!!.role)
            assertFalse(membershipUrl in dueUrls())
            assertFalse(personUrl in dueUrls())
        }
    }

    @Test
    fun `migration requeues existing raw resources and preserves list completion and active jobs`() {
        val list = listTarget()
        val objects = listedObjects()
        respondWithList(list, objects.values.toList())
        crawler.run(list.id)
        objects.values.forEach { dto -> targets.findByResource(resource(dto.id!!))!!.markAsCrawled() }
        val active = targets.findByResource(resource(objects.getValue(OparlType.PERSON).id!!))!!
        val jobId = UUID.randomUUID()
        active.markAsScheduled(jobId)
        val missing = targets.findByResource(resource(objects.getValue(OparlType.FILE).id!!))!!
        targets.delete(missing)
        entityManager.flush()

        jdbc.execute(ClassPathResource("db/migration/V9__recrawl_raw_resources.sql").getContentAsString(Charsets.UTF_8))
        entityManager.clear()

        val due = dueUrls()
        objects.forEach { (type, dto) ->
            val target = assertNotNull(targets.findByResource(resource(dto.id!!)))
            assertEquals(type, target.entityType)
            assertNull(target.lastCrawledAt)
            assertTrue(target.referenceCount > 0)
            if (type == OparlType.PERSON) {
                assertEquals(jobId, target.jobId)
                assertFalse(dto.id in due)
            } else {
                assertTrue(dto.id in due)
            }
        }
        assertNotNull(listTarget().lastCrawledAt)
        assertFalse(LIST_URL in due)
    }

    private fun listTarget() = targets.findById(LIST_TARGET_ID).orElseThrow()

    private fun resource(url: String) = OparlResource(listTarget().resource.system, OparlUrl(url))

    private fun dueUrls(): Set<String> {
        entityManager.flush()
        return targets.findDue(Instant.now(), 100).map { it.resource.url.value }.toSet()
    }

    private fun respondWithList(target: CrawlTarget, objects: List<OparlObjectDto>) {
        `when`(client.get(target.resource.url.value)).thenReturn(OparlListDto(data = objects))
    }

    private fun listedObjects(): Map<OparlType, OparlObjectDto> = mapOf(
        OparlType.AGENDA_ITEM to OparlAgendaItemDto(id = "$LIST_URL/agenda_item"),
        OparlType.BODY to OparlBodyDto(id = "$LIST_URL/body"),
        OparlType.CONSULTATION to OparlConsultationDto(id = "$LIST_URL/consultation"),
        OparlType.FILE to OparlFileDto(id = "$LIST_URL/file"),
        OparlType.LEGISLATIVE_TERM to OparlLegislativeTermDto(id = "$LIST_URL/legislative_term"),
        OparlType.LOCATION to OparlLocationDto(id = "$LIST_URL/location"),
        OparlType.MEETING to OparlMeetingDto(id = "$LIST_URL/meeting"),
        OparlType.MEMBERSHIP to OparlMembershipDto(id = "$LIST_URL/membership"),
        OparlType.ORGANIZATION to OparlOrganizationDto(id = "$LIST_URL/organization"),
        OparlType.PAPER to OparlPaperDto(id = "$LIST_URL/paper"),
        OparlType.PERSON to OparlPersonDto(id = "$LIST_URL/person"),
        OparlType.SYSTEM to OparlSystemDto(id = "$LIST_URL/system"),
    )

    companion object {
        private val LIST_TARGET_ID = UUID.fromString("40000000-0000-4000-8000-000000000001")
        private const val LIST_URL = "https://oparl-demo.example.org/oparl/v1.1/crawl-regression"
    }
}
