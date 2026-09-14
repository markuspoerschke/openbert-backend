package nrw.openbert.backend.crawl.application

import nrw.openbert.backend.crawl.application.processor.AgendaItemProcessor
import nrw.openbert.backend.crawl.application.processor.BodyProcessor
import nrw.openbert.backend.crawl.application.processor.ConsultationProcessor
import nrw.openbert.backend.crawl.application.processor.FileProcessor
import nrw.openbert.backend.crawl.application.processor.LegislativeTermProcessor
import nrw.openbert.backend.crawl.application.processor.LocationProcessor
import nrw.openbert.backend.crawl.application.processor.MeetingProcessor
import nrw.openbert.backend.crawl.application.processor.MembershipProcessor
import nrw.openbert.backend.crawl.application.processor.OrganizationProcessor
import nrw.openbert.backend.crawl.application.processor.PaperProcessor
import nrw.openbert.backend.crawl.application.processor.PersonProcessor
import nrw.openbert.backend.crawl.application.processor.SystemProcessor
import nrw.openbert.backend.crawl.domain.entity.CrawlEntityType
import nrw.openbert.backend.crawl.domain.repository.CrawlTargetRepository
import nrw.openbert.backend.crawl.infrastructure.client.OparlClient
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlAgendaItemDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlBodyDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlConsultationDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlFileDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlLegislativeTermDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlListDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlLocationDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlMeetingDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlMembershipDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlObjectDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlOrganizationDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlPaperDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlPersonDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlResponseDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlSystemDto
import nrw.openbert.backend.oparl.domain.entity.OparlSystem
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
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
        entityType: CrawlEntityType?,
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

    private fun entityTypeOf(dto: OparlObjectDto): CrawlEntityType = when (dto) {
        is OparlAgendaItemDto -> CrawlEntityType.AGENDA_ITEM
        is OparlBodyDto -> CrawlEntityType.BODY
        is OparlConsultationDto -> CrawlEntityType.CONSULTATION
        is OparlFileDto -> CrawlEntityType.FILE
        is OparlLegislativeTermDto -> CrawlEntityType.LEGISLATIVE_TERM
        is OparlLocationDto -> CrawlEntityType.LOCATION
        is OparlMeetingDto -> CrawlEntityType.MEETING
        is OparlMembershipDto -> CrawlEntityType.MEMBERSHIP
        is OparlOrganizationDto -> CrawlEntityType.ORGANIZATION
        is OparlPaperDto -> CrawlEntityType.PAPER
        is OparlPersonDto -> CrawlEntityType.PERSON
        is OparlSystemDto -> CrawlEntityType.SYSTEM
    }

    private fun resource(system: OparlSystem, url: String) =
        OparlResource(system, OparlUrl(url))
}
