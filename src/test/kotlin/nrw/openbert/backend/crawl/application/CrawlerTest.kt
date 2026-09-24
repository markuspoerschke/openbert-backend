package nrw.openbert.backend.crawl.application

import nrw.openbert.backend.parldata.adapter.inbound.crawler.processor.AgendaItemProcessor
import nrw.openbert.backend.parldata.adapter.inbound.crawler.processor.BodyProcessor
import nrw.openbert.backend.parldata.adapter.inbound.crawler.processor.ConsultationProcessor
import nrw.openbert.backend.parldata.adapter.inbound.crawler.processor.FileProcessor
import nrw.openbert.backend.parldata.adapter.inbound.crawler.processor.LegislativeTermProcessor
import nrw.openbert.backend.parldata.adapter.inbound.crawler.processor.LocationProcessor
import nrw.openbert.backend.parldata.adapter.inbound.crawler.processor.MeetingProcessor
import nrw.openbert.backend.parldata.adapter.inbound.crawler.processor.MembershipProcessor
import nrw.openbert.backend.parldata.adapter.inbound.crawler.processor.OrganizationProcessor
import nrw.openbert.backend.parldata.adapter.inbound.crawler.processor.PaperProcessor
import nrw.openbert.backend.parldata.adapter.inbound.crawler.processor.PersonProcessor
import nrw.openbert.backend.parldata.adapter.inbound.crawler.processor.SystemProcessor
import nrw.openbert.backend.parldata.adapter.inbound.crawler.CrawlTargetEnqueuer
import nrw.openbert.backend.parldata.adapter.inbound.crawler.Crawler
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.domain.entity.CrawlTarget
import nrw.openbert.backend.parldata.application.port.outbound.repository.CrawlTargetRepository
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.OparlClient
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlAgendaItemDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlBodyDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlConsultationDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlFileDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlLegislativeTermDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlListLinksDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlLocationDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlMeetingDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlMembershipDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlOrganizationDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlPaperDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlPersonDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlResponseDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlSystemDto
import nrw.openbert.backend.parldata.domain.entity.OparlSystem
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import java.time.Instant
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CrawlerTest {
    @Test
    fun `dispatches every object in an OParl list and marks the target as crawled`() {
        val fixture = Fixture()
        val target = fixture.target("https://oparl.example.org/objects")
        val targetUrl = target.resource.url.value
        val agendaItem = OparlAgendaItemDto(id = "$targetUrl/agenda-item")
        val body = OparlBodyDto(id = "$targetUrl/body")
        val consultation = OparlConsultationDto(id = "$targetUrl/consultation")
        val file = OparlFileDto(id = "$targetUrl/file")
        val legislativeTerm = OparlLegislativeTermDto(id = "$targetUrl/legislative-term")
        val location = OparlLocationDto(id = "$targetUrl/location")
        val meeting = OparlMeetingDto(id = "$targetUrl/meeting")
        val membership = OparlMembershipDto(id = "$targetUrl/membership")
        val organization = OparlOrganizationDto(id = "$targetUrl/organization")
        val paper = OparlPaperDto(id = "$targetUrl/paper")
        val person = OparlPersonDto(id = "$targetUrl/person")
        val systemDto = OparlSystemDto(id = "$targetUrl/system")
        val nextPage = "$targetUrl?page=2"
        fixture.respondWith(
            target,
            OparlListDto(
                data = listOf(
                    agendaItem,
                    body,
                    consultation,
                    file,
                    legislativeTerm,
                    location,
                    meeting,
                    membership,
                    organization,
                    paper,
                    person,
                    systemDto,
                ),
                links = OparlListLinksDto(next = nextPage),
            ),
        )

        fixture.crawler.run(target.id)

        verify(fixture.agendaItemProcessor).process(fixture.resource(agendaItem.id!!), agendaItem)
        verify(fixture.bodyProcessor).process(fixture.resource(body.id!!), body)
        verify(fixture.consultationProcessor).process(fixture.resource(consultation.id!!), consultation)
        verify(fixture.fileProcessor).process(fixture.resource(file.id!!), file)
        verify(fixture.legislativeTermProcessor).process(fixture.resource(legislativeTerm.id!!), legislativeTerm)
        verify(fixture.locationProcessor).process(fixture.resource(location.id!!), location)
        verify(fixture.meetingProcessor).process(fixture.resource(meeting.id!!), meeting)
        verify(fixture.membershipProcessor).process(fixture.resource(membership.id!!), membership)
        verify(fixture.organizationProcessor).process(fixture.resource(organization.id!!), organization)
        verify(fixture.paperProcessor).process(fixture.resource(paper.id!!), paper)
        verify(fixture.personProcessor).process(fixture.resource(person.id!!), person)
        verify(fixture.systemProcessor).process(fixture.resource(systemDto.id!!), systemDto)
        verify(fixture.crawlTargetEnqueuer).enqueue(fixture.resource(nextPage), OparlType.AGENDA_ITEM)
        assertNotNull(target.lastCrawledAt)
        assertNotNull(target.lastIndexedAt)
        assertEquals(0, target.referenceCount)
        assertNull(target.scheduledAt)
        assertNull(target.jobId)
        verify(fixture.crawlTargetRepository).save(target)
    }

    @Test
    fun `uses the crawl target URL when a single object has no id`() {
        val fixture = Fixture()
        val target = fixture.target("https://oparl.example.org/body/1")
        val targetUrl = target.resource.url.value
        val body = OparlBodyDto(name = "Example City")
        fixture.respondWith(target, body)

        fixture.crawler.run(target.id)

        verify(fixture.bodyProcessor).process(fixture.resource(targetUrl), body)
        assertNotNull(target.lastCrawledAt)
        assertNotNull(target.lastIndexedAt)
        verify(fixture.crawlTargetRepository).save(target)
    }

    @Test
    fun `does not mark the target as crawled when processing fails`() {
        val fixture = Fixture()
        val target = fixture.target("https://oparl.example.org/body/1")
        val targetUrl = target.resource.url.value
        val body = OparlBodyDto(id = targetUrl)
        val resource = fixture.resource(targetUrl)
        fixture.respondWith(target, body)
        doThrow(IllegalStateException("Processing failed"))
            .`when`(fixture.bodyProcessor)
            .process(resource, body)

        assertFailsWith<IllegalStateException> {
            fixture.crawler.run(target.id)
        }

        assertNull(target.lastCrawledAt)
        assertNull(target.lastIndexedAt)
        assertEquals(3, target.referenceCount)
        assertNotNull(target.scheduledAt)
        assertNotNull(target.jobId)
        verify(fixture.crawlTargetRepository, never()).save(target)
    }

    private class Fixture {
        val crawlTargetRepository: CrawlTargetRepository = mock(CrawlTargetRepository::class.java)
        val oparlClient: OparlClient = mock(OparlClient::class.java)
        val crawlTargetEnqueuer: CrawlTargetEnqueuer = mock(CrawlTargetEnqueuer::class.java)
        val agendaItemProcessor: AgendaItemProcessor = mock(AgendaItemProcessor::class.java)
        val bodyProcessor: BodyProcessor = mock(BodyProcessor::class.java)
        val consultationProcessor: ConsultationProcessor = mock(ConsultationProcessor::class.java)
        val fileProcessor: FileProcessor = mock(FileProcessor::class.java)
        val legislativeTermProcessor: LegislativeTermProcessor = mock(LegislativeTermProcessor::class.java)
        val locationProcessor: LocationProcessor = mock(LocationProcessor::class.java)
        val meetingProcessor: MeetingProcessor = mock(MeetingProcessor::class.java)
        val membershipProcessor: MembershipProcessor = mock(MembershipProcessor::class.java)
        val organizationProcessor: OrganizationProcessor = mock(OrganizationProcessor::class.java)
        val paperProcessor: PaperProcessor = mock(PaperProcessor::class.java)
        val personProcessor: PersonProcessor = mock(PersonProcessor::class.java)
        val systemProcessor: SystemProcessor = mock(SystemProcessor::class.java)
        val system = OparlSystem(OparlUrl("https://oparl.example.org/system"))

        val crawler = Crawler(
            crawlTargetRepository = crawlTargetRepository,
            oparlClient = oparlClient,
            crawlTargetEnqueuer = crawlTargetEnqueuer,
            agendaItemProcessor = agendaItemProcessor,
            bodyProcessor = bodyProcessor,
            consultationProcessor = consultationProcessor,
            fileProcessor = fileProcessor,
            legislativeTermProcessor = legislativeTermProcessor,
            locationProcessor = locationProcessor,
            meetingProcessor = meetingProcessor,
            membershipProcessor = membershipProcessor,
            organizationProcessor = organizationProcessor,
            paperProcessor = paperProcessor,
            personProcessor = personProcessor,
            systemProcessor = systemProcessor,
        )

        fun target(url: String) = CrawlTarget(
            resource = resource(url),
            referenceCount = 3,
            scheduledAt = Instant.parse("2026-07-31T08:00:00Z"),
            jobId = UUID.randomUUID(),
        )

        fun respondWith(
            target: CrawlTarget,
            response: OparlResponseDto,
        ) {
            org.mockito.Mockito.`when`(crawlTargetRepository.findById(target.id))
                .thenReturn(Optional.of(target))
            org.mockito.Mockito.`when`(oparlClient.get(target.resource.url.value))
                .thenReturn(response)
        }

        fun resource(url: String) = OparlResource(system, OparlUrl(url))
    }
}
