package nrw.openbert.backend.parldata.adapter.inbound.crawler

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
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.application.port.outbound.repository.CrawlTargetRepository
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
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlResponseDto
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlSystemDto
import nrw.openbert.backend.parldata.domain.entity.OparlSystem
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class Crawler(
    private val crawlTargetRepository: CrawlTargetRepository,
    private val oparlClient: OparlClient,
    private val crawlTargetEnqueuer: CrawlTargetEnqueuer,
    private val agendaItemProcessor: AgendaItemProcessor,
    private val bodyProcessor: BodyProcessor,
    private val consultationProcessor: ConsultationProcessor,
    private val fileProcessor: FileProcessor,
    private val legislativeTermProcessor: LegislativeTermProcessor,
    private val locationProcessor: LocationProcessor,
    private val meetingProcessor: MeetingProcessor,
    private val membershipProcessor: MembershipProcessor,
    private val organizationProcessor: OrganizationProcessor,
    private val paperProcessor: PaperProcessor,
    private val personProcessor: PersonProcessor,
    private val systemProcessor: SystemProcessor,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun run(targetId: UUID) {
        val crawlTarget = crawlTargetRepository
            .findById(targetId)
            .orElseThrow {
                throw IllegalArgumentException("Target with id $targetId does not exist")
            }

        logger.info("Crawling OParl resource {} (target {})", crawlTarget.resource.url.value, targetId)

        val oparlData = oparlClient.get(crawlTarget.resource.url.value)

        process(
            response = oparlData,
            system = crawlTarget.resource.system,
            fallbackUrl = crawlTarget.resource.url.value,
            entityType = crawlTarget.entityType,
        )

        crawlTarget.markAsCrawled()
        crawlTargetRepository.save(crawlTarget)
    }

    private fun process(
        response: OparlResponseDto,
        system: OparlSystem,
        fallbackUrl: String,
        entityType: OparlType?,
    ) {
        when (response) {
            is OparlListDto -> {
                response.data.orEmpty().forEach { dto ->
                    process(dto, resource(system, requireNotNull(dto.id) { "OParl list item has no id" }))
                }
                response.links?.next?.let { next ->
                    val listEntityType = entityType ?: response.data.orEmpty().firstOrNull()?.let(::entityTypeOf)
                    crawlTargetEnqueuer.enqueue(resource(system, next), listEntityType)
                }
            }

            is OparlObjectDto -> process(response, resource(system, response.id ?: fallbackUrl))
        }
    }

    private fun process(dto: OparlObjectDto, resource: OparlResource) {
        when (dto) {
            is OparlAgendaItemDto -> agendaItemProcessor.process(resource, dto)
            is OparlBodyDto -> bodyProcessor.process(resource, dto)
            is OparlConsultationDto -> consultationProcessor.process(resource, dto)
            is OparlFileDto -> fileProcessor.process(resource, dto)
            is OparlLegislativeTermDto -> legislativeTermProcessor.process(resource, dto)
            is OparlLocationDto -> locationProcessor.process(resource, dto)
            is OparlMeetingDto -> meetingProcessor.process(resource, dto)
            is OparlMembershipDto -> membershipProcessor.process(resource, dto)
            is OparlOrganizationDto -> organizationProcessor.process(resource, dto)
            is OparlPaperDto -> paperProcessor.process(resource, dto)
            is OparlPersonDto -> personProcessor.process(resource, dto)
            is OparlSystemDto -> systemProcessor.process(resource, dto)
        }
    }

    private fun entityTypeOf(dto: OparlObjectDto): OparlType = when (dto) {
        is OparlAgendaItemDto -> OparlType.AGENDA_ITEM
        is OparlBodyDto -> OparlType.BODY
        is OparlConsultationDto -> OparlType.CONSULTATION
        is OparlFileDto -> OparlType.FILE
        is OparlLegislativeTermDto -> OparlType.LEGISLATIVE_TERM
        is OparlLocationDto -> OparlType.LOCATION
        is OparlMeetingDto -> OparlType.MEETING
        is OparlMembershipDto -> OparlType.MEMBERSHIP
        is OparlOrganizationDto -> OparlType.ORGANIZATION
        is OparlPaperDto -> OparlType.PAPER
        is OparlPersonDto -> OparlType.PERSON
        is OparlSystemDto -> OparlType.SYSTEM
    }

    private fun resource(system: OparlSystem, url: String) = OparlResource(system, OparlUrl(url))
}
