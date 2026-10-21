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

    override fun process(resource: OparlResource, dto: OparlMeetingDto): RawMeeting {
        enqueueUrls(resource, OparlType.ORGANIZATION, dto.organization.orEmpty())
        enqueueUrls(resource, OparlType.PERSON, dto.participant.orEmpty())
        processInline(resource, dto.location, locationProcessor)
            ?.meetingUrls
            ?.addUrl(resource.url)
        processInline(resource, dto.invitation, fileProcessor)
            ?.meetingUrls
            ?.addUrl(resource.url)
        processInline(resource, dto.resultsProtocol, fileProcessor)
            ?.meetingUrls
            ?.addUrl(resource.url)
        processInline(resource, dto.verbatimProtocol, fileProcessor)
            ?.meetingUrls
            ?.addUrl(resource.url)
        processInline(resource, dto.auxiliaryFile, fileProcessor)
            .forEach { it.meetingUrls.addUrl(resource.url) }
        processInline(resource, dto.agendaItem, agendaItemProcessor)
            .forEach { it.meetingUrl = resource.url }

        return super.process(resource, dto)
    }

    override fun updateEntity(entity: RawMeeting, dto: OparlMeetingDto) {
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
