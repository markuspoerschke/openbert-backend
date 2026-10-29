package nrw.openbert.backend.parldata.adapter.inbound.crawler.processor

import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlAgendaItemDto
import nrw.openbert.backend.parldata.application.crawl.CrawlTargetEnqueuer
import nrw.openbert.backend.parldata.application.enrich.EnrichTargetEnqueuer
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawAgendaItemRepository
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawAgendaItem
import nrw.openbert.backend.parldata.domain.enums.OparlType
import org.springframework.stereotype.Service

@Service
class AgendaItemProcessor(
    val rawAgendaItemRepository: RawAgendaItemRepository,
    crawlTargetEnqueuer: CrawlTargetEnqueuer,
    private val fileProcessor: FileProcessor,
    enrichTargetEnqueuer: EnrichTargetEnqueuer,
) : AbstractProcessor<OparlAgendaItemDto, RawAgendaItem>(crawlTargetEnqueuer, enrichTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawAgendaItem? = rawAgendaItemRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawAgendaItem = RawAgendaItem(resource)

    override fun saveEntity(entity: RawAgendaItem) = rawAgendaItemRepository.save(entity)

    override fun processRelationships(
        resource: OparlResource,
        dto: OparlAgendaItemDto,
    ) {
        enqueueUrls(resource, OparlType.MEETING, dto.meeting)
        enqueueUrls(resource, OparlType.CONSULTATION, dto.consultation)
        processInline(resource, dto.resolutionFile, fileProcessor) { it.agendaItemUrls.addUrl(resource.url) }
        processInline(resource, dto.auxiliaryFile, fileProcessor) { it.agendaItemUrls.addUrl(resource.url) }
    }

    override fun updateEntity(
        entity: RawAgendaItem,
        dto: OparlAgendaItemDto,
    ) {
        entity.meetingUrl = dto.meeting?.toOparlUrl()
        entity.consultationUrl = dto.consultation?.toOparlUrl()
        entity.resolutionFileUrl = dto.resolutionFile?.id?.toOparlUrl()
        entity.auxiliaryFileUrls =
            dto.auxiliaryFile
                .orEmpty()
                .mapNotNull { it.id }
                .toMutableList()
        entity.number = dto.number
        entity.order = dto.order
        entity.name = dto.name
        entity.public = dto.public
        entity.result = dto.result
        entity.resolutionText = dto.resolutionText
        entity.start = dto.start?.toOparlDateTime()
        entity.end = dto.end?.toOparlDateTime()
        entity.license = dto.license
        entity.keyword = dto.keyword.orEmpty().toMutableList()
        entity.web = dto.web
    }
}
