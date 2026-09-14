package nrw.openbert.backend.crawl.application.processor

import nrw.openbert.backend.crawl.application.CrawlTargetEnqueuer
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlAgendaItemDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlBodyDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlConsultationDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlFileDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlLegislativeTermDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlLocationDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlMeetingDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlMembershipDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlOrganizationDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlPaperDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlPersonDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlSystemDto
import nrw.openbert.backend.resolve.application.ResolveTargetEnqueuer
import nrw.openbert.backend.oparl.domain.entity.OparlSystem
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.repository.raw.RawAgendaItemRepository
import nrw.openbert.backend.oparl.domain.repository.raw.RawBodyRepository
import nrw.openbert.backend.oparl.domain.repository.raw.RawConsultationRepository
import nrw.openbert.backend.oparl.domain.repository.raw.RawFileRepository
import nrw.openbert.backend.oparl.domain.repository.raw.RawLegislativeTermRepository
import nrw.openbert.backend.oparl.domain.repository.raw.RawLocationRepository
import nrw.openbert.backend.oparl.domain.repository.raw.RawMeetingRepository
import nrw.openbert.backend.oparl.domain.repository.raw.RawMembershipRepository
import nrw.openbert.backend.oparl.domain.repository.raw.RawOrganizationRepository
import nrw.openbert.backend.oparl.domain.repository.raw.RawPaperRepository
import nrw.openbert.backend.oparl.domain.repository.raw.RawPersonRepository
import nrw.openbert.backend.oparl.domain.repository.raw.RawSystemRepository
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import org.junit.jupiter.api.Test
import org.mockito.Answers
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockingDetails
import org.mockito.Mockito.verify
import kotlin.test.assertEquals

class ProcessorRelationshipTest {
    private val system = OparlSystem(OparlUrl("https://oparl.example.org/system"))
    private val crawlTargetEnqueuer = mock(CrawlTargetEnqueuer::class.java)
    private val resolveTargetEnqueuer = mock(ResolveTargetEnqueuer::class.java)
    private val fileProcessor = mock(FileProcessor::class.java)
    private val agendaItemProcessor = mock(AgendaItemProcessor::class.java)
    private val consultationProcessor = mock(ConsultationProcessor::class.java)
    private val legislativeTermProcessor = mock(LegislativeTermProcessor::class.java)
    private val locationProcessor = mock(LocationProcessor::class.java)
    private val membershipProcessor = mock(MembershipProcessor::class.java)

    @Test
    fun `enqueues every referenced OParl URL`() {
        val expected = linkedSetOf<String>()
        fun ref(name: String) = "https://oparl.example.org/references/$name".also(expected::add)

        agendaItemProcessor().process(
            resource("agenda-item"),
            OparlAgendaItemDto(
                meeting = ref("agenda-meeting"),
                consultation = ref("agenda-consultation"),
            ),
        )
        bodyProcessor().process(
            resource("body"),
            OparlBodyDto(
                system = ref("body-system"),
                organization = ref("body-organizations"),
                person = ref("body-persons"),
                meeting = ref("body-meetings"),
                paper = ref("body-papers"),
                equivalent = listOf(ref("body-equivalent-1"), ref("body-equivalent-2")),
                agendaItem = ref("body-agenda-items"),
                consultation = ref("body-consultations"),
                file = ref("body-files"),
                locationList = ref("body-locations"),
                legislativeTermList = ref("body-legislative-terms"),
                membership = ref("body-memberships"),
            ),
        )
        consultationProcessor().process(
            resource("consultation"),
            OparlConsultationDto(
                paper = ref("consultation-paper"),
                agendaItem = ref("consultation-agenda-item"),
                meeting = ref("consultation-meeting"),
                organization = listOf(
                    ref("consultation-organization-1"),
                    ref("consultation-organization-2"),
                ),
            ),
        )
        fileProcessor().process(
            resource("file"),
            OparlFileDto(
                masterFile = ref("file-master"),
                derivativeFile = listOf(ref("file-derivative")),
                meeting = listOf(ref("file-meeting")),
                agendaItem = listOf(ref("file-agenda-item")),
                paper = listOf(ref("file-paper")),
            ),
        )
        legislativeTermProcessor().process(
            resource("legislative-term"),
            OparlLegislativeTermDto(body = ref("legislative-term-body")),
        )
        locationProcessor().process(
            resource("location"),
            OparlLocationDto(
                bodies = listOf(ref("location-body")),
                organizations = listOf(ref("location-organization")),
                persons = listOf(ref("location-person")),
                meetings = listOf(ref("location-meeting")),
                papers = listOf(ref("location-paper")),
            ),
        )
        meetingProcessor().process(
            resource("meeting"),
            OparlMeetingDto(
                organization = listOf(ref("meeting-organization")),
                participant = listOf(ref("meeting-participant")),
            ),
        )
        membershipProcessor().process(
            resource("membership"),
            OparlMembershipDto(
                person = ref("membership-person"),
                organization = ref("membership-organization"),
                onBehalfOf = ref("membership-on-behalf-of"),
            ),
        )
        organizationProcessor().process(
            resource("organization"),
            OparlOrganizationDto(
                body = ref("organization-body"),
                membership = listOf(ref("organization-membership")),
                meeting = ref("organization-meetings"),
                consultation = ref("organization-consultations"),
                subOrganizationOf = ref("organization-parent"),
                externalBody = ref("organization-external-body"),
            ),
        )
        paperProcessor().process(
            resource("paper"),
            OparlPaperDto(
                body = ref("paper-body"),
                relatedPaper = listOf(ref("paper-related")),
                superordinatedPaper = listOf(ref("paper-superordinated")),
                subordinatedPaper = listOf(ref("paper-subordinated")),
                originatorPerson = listOf(ref("paper-originator-person")),
                underDirectionOf = listOf(ref("paper-under-direction-of")),
                originatorOrganization = listOf(ref("paper-originator-organization")),
            ),
        )
        personProcessor().process(
            resource("person"),
            OparlPersonDto(
                body = ref("person-body"),
                location = ref("person-location"),
            ),
        )
        systemProcessor().process(
            resource("system"),
            OparlSystemDto(body = ref("system-bodies")),
        )

        val actual = mockingDetails(crawlTargetEnqueuer).invocations
            .filter { it.method.name == "enqueue" }
            .map { (it.arguments[0] as OparlResource).url.value }

        assertEquals(expected, actual.toSet())
        assertEquals(expected.size, actual.size)
    }

    @Test
    fun `processes every inline relationship immediately`() {
        val resolutionFile = OparlFileDto(id = inlineUrl("agenda-resolution"))
        val agendaAuxiliaryFile = OparlFileDto(id = inlineUrl("agenda-auxiliary"))
        agendaItemProcessor().process(
            resource("agenda-item"),
            OparlAgendaItemDto(
                resolutionFile = resolutionFile,
                auxiliaryFile = listOf(agendaAuxiliaryFile),
            ),
        )

        val legislativeTerm = OparlLegislativeTermDto(id = inlineUrl("body-legislative-term"))
        val bodyLocation = OparlLocationDto(id = inlineUrl("body-location"))
        bodyProcessor().process(
            resource("body"),
            OparlBodyDto(
                legislativeTerm = listOf(legislativeTerm),
                location = bodyLocation,
            ),
        )

        val meetingLocation = OparlLocationDto(id = inlineUrl("meeting-location"))
        val invitation = OparlFileDto(id = inlineUrl("meeting-invitation"))
        val resultsProtocol = OparlFileDto(id = inlineUrl("meeting-results"))
        val verbatimProtocol = OparlFileDto(id = inlineUrl("meeting-verbatim"))
        val meetingAuxiliaryFile = OparlFileDto(id = inlineUrl("meeting-auxiliary"))
        val agendaItem = OparlAgendaItemDto(id = inlineUrl("meeting-agenda-item"))
        meetingProcessor().process(
            resource("meeting"),
            OparlMeetingDto(
                location = meetingLocation,
                invitation = invitation,
                resultsProtocol = resultsProtocol,
                verbatimProtocol = verbatimProtocol,
                auxiliaryFile = listOf(meetingAuxiliaryFile),
                agendaItem = listOf(agendaItem),
            ),
        )

        val organizationLocation = OparlLocationDto(id = inlineUrl("organization-location"))
        organizationProcessor().process(
            resource("organization"),
            OparlOrganizationDto(location = organizationLocation),
        )

        val mainFile = OparlFileDto(id = inlineUrl("paper-main-file"))
        val paperAuxiliaryFile = OparlFileDto(id = inlineUrl("paper-auxiliary"))
        val paperLocation = OparlLocationDto(id = inlineUrl("paper-location"))
        val consultation = OparlConsultationDto(id = inlineUrl("paper-consultation"))
        paperProcessor().process(
            resource("paper"),
            OparlPaperDto(
                mainFile = mainFile,
                auxiliaryFile = listOf(paperAuxiliaryFile),
                location = listOf(paperLocation),
                consultation = listOf(consultation),
            ),
        )

        val personLocation = OparlLocationDto(id = inlineUrl("person-location"))
        val membership = OparlMembershipDto(id = inlineUrl("person-membership"))
        personProcessor().process(
            resource("person"),
            OparlPersonDto(
                locationObject = personLocation,
                membership = listOf(membership),
            ),
        )

        verify(fileProcessor).process(resource(resolutionFile.id!!), resolutionFile)
        verify(fileProcessor).process(resource(agendaAuxiliaryFile.id!!), agendaAuxiliaryFile)
        verify(legislativeTermProcessor).process(resource(legislativeTerm.id!!), legislativeTerm)
        verify(locationProcessor).process(resource(bodyLocation.id!!), bodyLocation)
        verify(locationProcessor).process(resource(meetingLocation.id!!), meetingLocation)
        verify(fileProcessor).process(resource(invitation.id!!), invitation)
        verify(fileProcessor).process(resource(resultsProtocol.id!!), resultsProtocol)
        verify(fileProcessor).process(resource(verbatimProtocol.id!!), verbatimProtocol)
        verify(fileProcessor).process(resource(meetingAuxiliaryFile.id!!), meetingAuxiliaryFile)
        verify(agendaItemProcessor).process(resource(agendaItem.id!!), agendaItem)
        verify(locationProcessor).process(resource(organizationLocation.id!!), organizationLocation)
        verify(fileProcessor).process(resource(mainFile.id!!), mainFile)
        verify(fileProcessor).process(resource(paperAuxiliaryFile.id!!), paperAuxiliaryFile)
        verify(locationProcessor).process(resource(paperLocation.id!!), paperLocation)
        verify(consultationProcessor).process(resource(consultation.id!!), consultation)
        verify(locationProcessor).process(resource(personLocation.id!!), personLocation)
        verify(membershipProcessor).process(resource(membership.id!!), membership)
    }

    private fun agendaItemProcessor() = AgendaItemProcessor(
        processorRepository(RawAgendaItemRepository::class.java),
        crawlTargetEnqueuer,
        fileProcessor,
        resolveTargetEnqueuer,
    )

    private fun bodyProcessor() = BodyProcessor(
        processorRepository(RawBodyRepository::class.java),
        crawlTargetEnqueuer,
        legislativeTermProcessor,
        locationProcessor,
        resolveTargetEnqueuer,
    )

    private fun consultationProcessor() = ConsultationProcessor(
        processorRepository(RawConsultationRepository::class.java),
        crawlTargetEnqueuer,
        resolveTargetEnqueuer,
    )

    private fun fileProcessor() = FileProcessor(
        processorRepository(RawFileRepository::class.java),
        crawlTargetEnqueuer,
        resolveTargetEnqueuer,
    )

    private fun legislativeTermProcessor() = LegislativeTermProcessor(
        processorRepository(RawLegislativeTermRepository::class.java),
        crawlTargetEnqueuer,
        resolveTargetEnqueuer,
    )

    private fun locationProcessor() = LocationProcessor(
        processorRepository(RawLocationRepository::class.java),
        crawlTargetEnqueuer,
        resolveTargetEnqueuer,
    )

    private fun meetingProcessor() = MeetingProcessor(
        processorRepository(RawMeetingRepository::class.java),
        crawlTargetEnqueuer,
        locationProcessor,
        fileProcessor,
        agendaItemProcessor,
        resolveTargetEnqueuer,
    )

    private fun membershipProcessor() = MembershipProcessor(
        processorRepository(RawMembershipRepository::class.java),
        crawlTargetEnqueuer,
        resolveTargetEnqueuer,
    )

    private fun organizationProcessor() = OrganizationProcessor(
        processorRepository(RawOrganizationRepository::class.java),
        crawlTargetEnqueuer,
        locationProcessor,
        resolveTargetEnqueuer,
    )

    private fun paperProcessor() = PaperProcessor(
        processorRepository(RawPaperRepository::class.java),
        crawlTargetEnqueuer,
        fileProcessor,
        locationProcessor,
        consultationProcessor,
        resolveTargetEnqueuer,
    )

    private fun personProcessor() = PersonProcessor(
        processorRepository(RawPersonRepository::class.java),
        crawlTargetEnqueuer,
        locationProcessor,
        membershipProcessor,
        resolveTargetEnqueuer,
    )

    private fun systemProcessor() = SystemProcessor(
        processorRepository(RawSystemRepository::class.java),
        crawlTargetEnqueuer,
        resolveTargetEnqueuer,
    )

    private fun resource(pathOrUrl: String): OparlResource {
        val url = if (pathOrUrl.startsWith("https://")) {
            pathOrUrl
        } else {
            "https://oparl.example.org/$pathOrUrl"
        }
        return OparlResource(system, OparlUrl(url))
    }

    private fun inlineUrl(name: String) = "https://oparl.example.org/inline/$name"

    private fun <T> processorRepository(repositoryType: Class<T>): T =
        mock(repositoryType) { invocation ->
            if (invocation.method.name == "save") {
                invocation.arguments.first()
            } else {
                Answers.RETURNS_DEFAULTS.answer(invocation)
            }
        }
}
