package nrw.openbert.backend.crawl.application.processor

import nrw.openbert.backend.crawl.application.CrawlTargetEnqueuer
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlAgendaItemDto
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawAgendaItem
import nrw.openbert.backend.oparl.domain.repository.raw.RawAgendaItemRepository
import org.springframework.stereotype.Service

@Service
class AgendaItemProcessor(
    val rawAgendaItemRepository: RawAgendaItemRepository,
    crawlTargetEnqueuer: CrawlTargetEnqueuer,
    private val fileProcessor: FileProcessor,
) : AbstractProcessor<OparlAgendaItemDto, RawAgendaItem>(crawlTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawAgendaItem? =
        rawAgendaItemRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawAgendaItem = RawAgendaItem(resource)

    override fun saveEntity(entity: RawAgendaItem) = rawAgendaItemRepository.save(entity)

    override fun process(resource: OparlResource, dto: OparlAgendaItemDto): RawAgendaItem {
        enqueueUrls(resource, dto.meeting, dto.consultation)
        processInline(resource, dto.resolutionFile, fileProcessor)
        processInline(resource, dto.auxiliaryFile, fileProcessor)

        return super.process(resource, dto)
    }

    override fun updateEntity(entity: RawAgendaItem, dto: OparlAgendaItemDto) {
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
