package nrw.openbert.backend.crawl.application.processor

import nrw.openbert.backend.parldata.adapter.inbound.crawler.CrawlTargetEnqueuer
import nrw.openbert.backend.parldata.adapter.inbound.crawler.processor.AgendaItemProcessor
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
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlAgendaItemDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlConsultationDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlFileDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlLegislativeTermDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlLocationDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlMeetingDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlMembershipDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlOrganizationDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlPaperDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlPersonDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlSystemDto
import nrw.openbert.backend.parldata.application.resolve.ResolveTargetEnqueuer
import nrw.openbert.backend.parldata.domain.entity.OparlSystem
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawAgendaItemRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawConsultationRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawFileRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawLegislativeTermRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawLocationRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawMeetingRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawMembershipRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawOrganizationRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawPaperRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawPersonRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawSystemRepository
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.junit.jupiter.api.Test
import org.mockito.Answers
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.time.Instant
import java.util.Date
import kotlin.test.assertEquals

class RawEntityProcessorsTest {
    private val system = OparlSystem(OparlUrl("https://oparl.example.org"))
    private val crawlTargetEnqueuer = mock(CrawlTargetEnqueuer::class.java)
    private val resolveTargetEnqueuer = mock(ResolveTargetEnqueuer::class.java)
    private val agendaItemProcessor = mock(AgendaItemProcessor::class.java)
    private val consultationProcessor = mock(ConsultationProcessor::class.java)
    private val fileProcessor = mock(FileProcessor::class.java)
    private val legislativeTermProcessor = mock(LegislativeTermProcessor::class.java)
    private val locationProcessor = mock(LocationProcessor::class.java)
    private val membershipProcessor = mock(MembershipProcessor::class.java)

    @Test
    fun `processes an agenda item`() {
        val repository = processorRepository(RawAgendaItemRepository::class.java)
        val entity = AgendaItemProcessor(repository, crawlTargetEnqueuer, fileProcessor, resolveTargetEnqueuer).process(
            resource("agenda-item"),
            OparlAgendaItemDto(
                number = "10.1",
                order = 3,
                name = "Budget",
                public = true,
                result = "Approved",
                resolutionText = "Resolution",
                start = "2026-07-24T09:15:00+02:00",
                end = "2026-07-24T10:00:00+02:00",
                license = "CC0-1.0",
                keyword = listOf("budget"),
                web = "https://example.org/agenda-item",
            ),
        )

        assertEquals("10.1", entity.number)
        assertEquals(3, entity.order)
        assertEquals("Budget", entity.name)
        assertEquals(true, entity.public)
        assertEquals("Approved", entity.result)
        assertEquals("Resolution", entity.resolutionText)
        assertEquals(dateTime("2026-07-24T07:15:00Z"), entity.start)
        assertEquals(dateTime("2026-07-24T08:00:00Z"), entity.end)
        assertEquals("CC0-1.0", entity.license)
        assertEquals(listOf("budget"), entity.keyword)
        assertEquals("https://example.org/agenda-item", entity.web)
        verify(repository).save(entity)
        verify(crawlTargetEnqueuer).markAsCrawled(entity.resource)
    }

    @Test
    fun `processes a consultation`() {
        val repository = processorRepository(RawConsultationRepository::class.java)
        val entity = ConsultationProcessor(repository, crawlTargetEnqueuer, resolveTargetEnqueuer).process(
            resource("consultation"),
            OparlConsultationDto(
                authoritative = true,
                role = "Decision",
                license = "CC0-1.0",
                keyword = listOf("budget"),
                web = "https://example.org/consultation",
            ),
        )

        assertEquals(true, entity.authoritative)
        assertEquals("Decision", entity.role)
        assertEquals("CC0-1.0", entity.license)
        assertEquals(listOf("budget"), entity.keyword)
        assertEquals("https://example.org/consultation", entity.web)
        verify(repository).save(entity)
        verify(crawlTargetEnqueuer).markAsCrawled(entity.resource)
    }

    @Test
    fun `processes a file`() {
        val repository = processorRepository(RawFileRepository::class.java)
        val entity = FileProcessor(repository, crawlTargetEnqueuer, resolveTargetEnqueuer).process(
            resource("file"),
            OparlFileDto(
                name = "Budget",
                fileName = "budget.pdf",
                mimeType = "application/pdf",
                date = "2026-07-24",
                size = 42,
                sha1Checksum = "sha1",
                sha512Checksum = "sha512",
                text = "Budget document",
                accessUrl = "https://example.org/files/1",
                downloadUrl = "https://example.org/files/1/download",
                externalServiceUrl = "https://example.org/external/1",
                fileLicense = "https://example.org/licenses/1",
                license = "CC0-1.0",
                keyword = listOf("budget"),
                web = "https://example.org/file",
            ),
        )

        assertEquals("Budget", entity.name)
        assertEquals("budget.pdf", entity.fileName)
        assertEquals("application/pdf", entity.mimeType)
        assertEquals(date("2026-07-24T00:00:00Z"), entity.date)
        assertEquals(42, entity.size)
        assertEquals("sha1", entity.sha1Checksum)
        assertEquals("sha512", entity.sha512Checksum)
        assertEquals("Budget document", entity.text)
        assertEquals("https://example.org/files/1", entity.accessUrl?.value)
        assertEquals("https://example.org/files/1/download", entity.downloadUrl?.value)
        assertEquals("https://example.org/external/1", entity.externalServiceUrl?.value)
        assertEquals("https://example.org/licenses/1", entity.fileLicense?.value)
        assertEquals("CC0-1.0", entity.license)
        assertEquals(listOf("budget"), entity.keyword)
        assertEquals("https://example.org/file", entity.web)
        verify(repository).save(entity)
        verify(crawlTargetEnqueuer).markAsCrawled(entity.resource)
    }

    @Test
    fun `processes a legislative term`() {
        val repository = processorRepository(RawLegislativeTermRepository::class.java)
        val entity = LegislativeTermProcessor(repository, crawlTargetEnqueuer, resolveTargetEnqueuer).process(
            resource("legislative-term"),
            OparlLegislativeTermDto(
                body = "https://example.org/body/1",
                name = "2025–2030",
                startDate = "2025-11-01",
                endDate = "2030-10-31",
                license = "CC0-1.0",
                keyword = listOf("term"),
                web = "https://example.org/legislative-term",
            ),
        )

        assertEquals("https://example.org/body/1", entity.bodyUrl?.value)
        assertEquals("2025–2030", entity.name)
        assertEquals(date("2025-11-01T00:00:00Z"), entity.startDate)
        assertEquals(date("2030-10-31T00:00:00Z"), entity.endDate)
        assertEquals("CC0-1.0", entity.license)
        assertEquals(listOf("term"), entity.keyword)
        assertEquals("https://example.org/legislative-term", entity.web)
        verify(repository).save(entity)
        verify(crawlTargetEnqueuer).markAsCrawled(entity.resource)
    }

    @Test
    fun `processes a location`() {
        val repository = processorRepository(RawLocationRepository::class.java)
        val geojson = mapOf<String, Any?>("type" to "Point", "coordinates" to listOf(1, 2))
        val entity = LocationProcessor(repository, crawlTargetEnqueuer, resolveTargetEnqueuer).process(
            resource("location"),
            OparlLocationDto(
                description = "City hall",
                geojson = geojson,
                streetAddress = "Main Street 1",
                room = "Council chamber",
                postalCode = "12345",
                subLocality = "Centre",
                locality = "Example City",
                license = "CC0-1.0",
                keyword = listOf("city hall"),
                web = "https://example.org/location",
            ),
        )

        assertEquals("City hall", entity.description)
        assertEquals(geojson, entity.geojson)
        assertEquals("Main Street 1", entity.streetAddress)
        assertEquals("Council chamber", entity.room)
        assertEquals("12345", entity.postalCode)
        assertEquals("Centre", entity.subLocality)
        assertEquals("Example City", entity.locality)
        assertEquals("CC0-1.0", entity.license)
        assertEquals(listOf("city hall"), entity.keyword)
        assertEquals("https://example.org/location", entity.web)
        verify(repository).save(entity)
        verify(crawlTargetEnqueuer).markAsCrawled(entity.resource)
    }

    @Test
    fun `processes a meeting`() {
        val repository = processorRepository(RawMeetingRepository::class.java)
        val entity = MeetingProcessor(
            repository,
            crawlTargetEnqueuer,
            locationProcessor,
            fileProcessor,
            agendaItemProcessor,
            resolveTargetEnqueuer,
        ).process(
            resource("meeting"),
            OparlMeetingDto(
                name = "Council meeting",
                meetingState = "completed",
                cancelled = false,
                start = "2026-07-24T09:15:00+02:00",
                end = "2026-07-24T10:00:00+02:00",
                license = "CC0-1.0",
                keyword = listOf("council"),
                web = "https://example.org/meeting",
            ),
        )

        assertEquals("Council meeting", entity.name)
        assertEquals("completed", entity.meetingState)
        assertEquals(false, entity.cancelled)
        assertEquals(dateTime("2026-07-24T07:15:00Z"), entity.start)
        assertEquals(dateTime("2026-07-24T08:00:00Z"), entity.end)
        assertEquals("CC0-1.0", entity.license)
        assertEquals(listOf("council"), entity.keyword)
        assertEquals("https://example.org/meeting", entity.web)
        verify(repository).save(entity)
        verify(crawlTargetEnqueuer).markAsCrawled(entity.resource)
    }

    @Test
    fun `processes a membership`() {
        val repository = processorRepository(RawMembershipRepository::class.java)
        val entity = MembershipProcessor(repository, crawlTargetEnqueuer, resolveTargetEnqueuer).process(
            resource("membership"),
            OparlMembershipDto(
                role = "Chair",
                votingRight = true,
                startDate = "2025-11-01",
                endDate = "2030-10-31",
                license = "CC0-1.0",
                keyword = listOf("chair"),
                web = "https://example.org/membership",
            ),
        )

        assertEquals("Chair", entity.role)
        assertEquals(true, entity.votingRight)
        assertEquals(date("2025-11-01T00:00:00Z"), entity.startDate)
        assertEquals(date("2030-10-31T00:00:00Z"), entity.endDate)
        assertEquals("CC0-1.0", entity.license)
        assertEquals(listOf("chair"), entity.keyword)
        assertEquals("https://example.org/membership", entity.web)
        verify(repository).save(entity)
        verify(crawlTargetEnqueuer).markAsCrawled(entity.resource)
    }

    @Test
    fun `processes an organization`() {
        val repository = processorRepository(RawOrganizationRepository::class.java)
        val entity = OrganizationProcessor(repository, crawlTargetEnqueuer, locationProcessor, resolveTargetEnqueuer).process(
            resource("organization"),
            OparlOrganizationDto(
                body = "https://example.org/body/1",
                name = "Finance committee",
                shortName = "Finance",
                post = listOf("Chair"),
                organizationType = "Committee",
                classification = "Finance committee",
                startDate = "2025-11-01",
                endDate = "2030-10-31",
                website = "https://example.org/finance",
                license = "CC0-1.0",
                keyword = listOf("finance"),
                web = "https://example.org/organization",
            ),
        )

        assertEquals("https://example.org/body/1", entity.bodyUrl?.value)
        assertEquals("Finance committee", entity.name)
        assertEquals("Finance", entity.shortName)
        assertEquals(listOf("Chair"), entity.post)
        assertEquals("Committee", entity.organizationType)
        assertEquals("Finance committee", entity.classification)
        assertEquals(date("2025-11-01T00:00:00Z"), entity.startDate)
        assertEquals(date("2030-10-31T00:00:00Z"), entity.endDate)
        assertEquals("https://example.org/finance", entity.website?.value)
        assertEquals("CC0-1.0", entity.license)
        assertEquals(listOf("finance"), entity.keyword)
        assertEquals("https://example.org/organization", entity.web)
        verify(repository).save(entity)
        verify(crawlTargetEnqueuer).markAsCrawled(entity.resource)
    }

    @Test
    fun `processes a paper`() {
        val repository = processorRepository(RawPaperRepository::class.java)
        val entity = PaperProcessor(
            repository,
            crawlTargetEnqueuer,
            fileProcessor,
            locationProcessor,
            consultationProcessor,
            resolveTargetEnqueuer,
        ).process(
            resource("paper"),
            OparlPaperDto(
                body = "https://example.org/body/1",
                name = "Budget 2027",
                reference = "V-2026-42",
                date = "2026-07-24",
                paperType = "Proposal",
                license = "CC0-1.0",
                keyword = listOf("budget"),
                web = "https://example.org/paper",
            ),
        )

        assertEquals("https://example.org/body/1", entity.bodyUrl?.value)
        assertEquals("Budget 2027", entity.name)
        assertEquals("V-2026-42", entity.reference)
        assertEquals(date("2026-07-24T00:00:00Z"), entity.date)
        assertEquals("Proposal", entity.paperType)
        assertEquals("CC0-1.0", entity.license)
        assertEquals(listOf("budget"), entity.keyword)
        assertEquals("https://example.org/paper", entity.web)
        verify(repository).save(entity)
        verify(crawlTargetEnqueuer).markAsCrawled(entity.resource)
    }

    @Test
    fun `processes a person`() {
        val repository = processorRepository(RawPersonRepository::class.java)
        val entity = PersonProcessor(
            repository,
            crawlTargetEnqueuer,
            locationProcessor,
            membershipProcessor,
            resolveTargetEnqueuer,
        ).process(
            resource("person"),
            OparlPersonDto(
                body = "https://example.org/body/1",
                name = "Dr Jane Doe",
                familyName = "Doe",
                givenName = "Jane",
                formOfAddress = "Dr",
                affix = "PhD",
                title = listOf("Councillor"),
                gender = "female",
                phone = listOf("+49 123"),
                email = listOf("jane@example.org"),
                status = listOf("active"),
                life = "Biography",
                lifeSource = "https://example.org/biography",
                license = "CC0-1.0",
                keyword = listOf("councillor"),
                web = "https://example.org/person",
            ),
        )

        assertEquals("https://example.org/body/1", entity.bodyUrl?.value)
        assertEquals("Dr Jane Doe", entity.name)
        assertEquals("Doe", entity.familyName)
        assertEquals("Jane", entity.givenName)
        assertEquals("Dr", entity.formOfAddress)
        assertEquals("PhD", entity.affix)
        assertEquals(listOf("Councillor"), entity.title)
        assertEquals("female", entity.gender)
        assertEquals(listOf("+49 123"), entity.phone)
        assertEquals(listOf("jane@example.org"), entity.email)
        assertEquals(listOf("active"), entity.status)
        assertEquals("Biography", entity.life)
        assertEquals("https://example.org/biography", entity.lifeSource)
        assertEquals("CC0-1.0", entity.license)
        assertEquals(listOf("councillor"), entity.keyword)
        assertEquals("https://example.org/person", entity.web)
        verify(repository).save(entity)
        verify(crawlTargetEnqueuer).markAsCrawled(entity.resource)
    }

    @Test
    fun `processes a system`() {
        val repository = processorRepository(RawSystemRepository::class.java)
        val entity = SystemProcessor(repository, crawlTargetEnqueuer, resolveTargetEnqueuer).process(
            resource("system"),
            OparlSystemDto(
                oparlVersion = "https://schema.oparl.org/1.1/",
                otherOparlVersions = listOf("https://schema.oparl.org/1.0/"),
                license = "https://example.org/license",
                name = "Example OParl",
                contactEmail = "oparl@example.org",
                contactName = "Jane Doe",
                website = "https://example.org",
                vendor = "https://vendor.example.org",
                product = "https://product.example.org",
                web = "https://example.org/system",
            ),
        )

        assertEquals("https://schema.oparl.org/1.1/", entity.oparlVersion)
        assertEquals(listOf("https://schema.oparl.org/1.0/"), entity.otherOparlVersions)
        assertEquals("https://example.org/license", entity.license?.value)
        assertEquals("Example OParl", entity.name)
        assertEquals("oparl@example.org", entity.contactEmail)
        assertEquals("Jane Doe", entity.contactName)
        assertEquals("https://example.org", entity.website?.value)
        assertEquals("https://vendor.example.org", entity.vendor?.value)
        assertEquals("https://product.example.org", entity.product?.value)
        assertEquals("https://example.org/system", entity.web)
        verify(repository).save(entity)
        verify(crawlTargetEnqueuer).markAsCrawled(entity.resource)
        verify(resolveTargetEnqueuer).enqueue(entity)
    }

    private fun resource(path: String) =
        OparlResource(system, OparlUrl("https://oparl.example.org/$path"))

    private fun <T> processorRepository(repositoryType: Class<T>): T =
        mock(repositoryType) { invocation ->
            if (invocation.method.name == "save") {
                invocation.arguments.first()
            } else {
                Answers.RETURNS_DEFAULTS.answer(invocation)
            }
        }

    private fun date(value: String): Date = Date.from(Instant.parse(value))

    private fun dateTime(value: String): Date = Date.from(Instant.parse(value))
}
