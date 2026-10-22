package nrw.openbert.backend.parldata.adapter.inbound.crawler.processor

import nrw.openbert.backend.parldata.application.crawl.CrawlTargetEnqueuer
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlMeetingDto
import nrw.openbert.backend.parldata.application.enrich.EnrichTargetEnqueuer
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawMeeting
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawMeetingRepository
import org.springframework.stereotype.Service

@Service
class MeetingProcessor(
    val rawMeetingRepository: RawMeetingRepository,
    crawlTargetEnqueuer: CrawlTargetEnqueuer,
    private val locationProcessor: LocationProcessor,
    private val fileProcessor: FileProcessor,
    private val agendaItemProcessor: AgendaItemProcessor,
    enrichTargetEnqueuer: EnrichTargetEnqueuer,
) : AbstractProcessor<OparlMeetingDto, RawMeeting>(crawlTargetEnqueuer, enrichTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawMeeting? = rawMeetingRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawMeeting = RawMeeting(resource)

    override fun saveEntity(entity: RawMeeting) = rawMeetingRepository.save(entity)

    override fun processRelationships(resource: OparlResource, dto: OparlMeetingDto) {
        enqueueUrls(resource, OparlType.BODY, dto.body)
        enqueueUrls(resource, OparlType.ORGANIZATION, dto.organization.orEmpty())
        enqueueUrls(resource, OparlType.PERSON, dto.participant.orEmpty())
        processInline(resource, dto.location, locationProcessor) { it.meetingUrls.addUrl(resource.url) }
        processInline(resource, dto.invitation, fileProcessor) { it.meetingUrls.addUrl(resource.url) }
        processInline(resource, dto.resultsProtocol, fileProcessor) { it.meetingUrls.addUrl(resource.url) }
        processInline(resource, dto.verbatimProtocol, fileProcessor) { it.meetingUrls.addUrl(resource.url) }
        processInline(resource, dto.auxiliaryFile, fileProcessor) { it.meetingUrls.addUrl(resource.url) }
        processInline(resource, dto.agendaItem, agendaItemProcessor) { it.meetingUrl = resource.url }
    }

    override fun updateEntity(entity: RawMeeting, dto: OparlMeetingDto) {
        entity.bodyUrl = dto.body?.toOparlUrl()
        entity.organizationUrls = dto.organization.orEmpty().toMutableList()
        entity.participantUrls = dto.participant.orEmpty().toMutableList()
        entity.name = dto.name
        entity.meetingState = dto.meetingState
        entity.cancelled = dto.cancelled
        entity.start = dto.start?.toOparlDateTime()
        entity.end = dto.end?.toOparlDateTime()
        entity.license = dto.license
        entity.keyword = dto.keyword.orEmpty().toMutableList()
        entity.web = dto.web
    }
}
