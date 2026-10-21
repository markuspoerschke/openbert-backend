package nrw.openbert.backend.parldata.adapter.inbound.crawler.processor

import nrw.openbert.backend.parldata.application.crawl.CrawlTargetEnqueuer
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlLegislativeTermDto
import nrw.openbert.backend.parldata.application.enrich.EnrichTargetEnqueuer
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawLegislativeTerm
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawLegislativeTermRepository
import org.springframework.stereotype.Service

@Service
class LegislativeTermProcessor(
    val rawLegislativeTermRepository: RawLegislativeTermRepository,
    crawlTargetEnqueuer: CrawlTargetEnqueuer,
    enrichTargetEnqueuer: EnrichTargetEnqueuer,
) : AbstractProcessor<OparlLegislativeTermDto, RawLegislativeTerm>(crawlTargetEnqueuer, enrichTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawLegislativeTerm? =
        rawLegislativeTermRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawLegislativeTerm = RawLegislativeTerm(resource)

    override fun saveEntity(entity: RawLegislativeTerm) = rawLegislativeTermRepository.save(entity)

    override fun process(resource: OparlResource, dto: OparlLegislativeTermDto): RawLegislativeTerm {
        enqueueUrls(resource, OparlType.BODY, dto.body)

        return super.process(resource, dto)
    }

    override fun updateEntity(entity: RawLegislativeTerm, dto: OparlLegislativeTermDto) {
        entity.bodyUrl = dto.body?.toOparlUrl()
        entity.name = dto.name
        entity.startDate = dto.startDate?.toOparlDate()
        entity.endDate = dto.endDate?.toOparlDate()
        entity.license = dto.license
        entity.keyword = dto.keyword.orEmpty().toMutableList()
        entity.web = dto.web
    }
}
