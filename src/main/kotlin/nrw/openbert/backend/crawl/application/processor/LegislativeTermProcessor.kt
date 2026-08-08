package nrw.openbert.backend.crawl.application.processor

import nrw.openbert.backend.crawl.application.CrawlTargetEnqueuer
import nrw.openbert.backend.crawl.domain.entity.CrawlEntityType
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlLegislativeTermDto
import nrw.openbert.backend.resolve.application.ResolveTargetEnqueuer
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawLegislativeTerm
import nrw.openbert.backend.oparl.domain.repository.raw.RawLegislativeTermRepository
import org.springframework.stereotype.Service

@Service
class LegislativeTermProcessor(
    val rawLegislativeTermRepository: RawLegislativeTermRepository,
    crawlTargetEnqueuer: CrawlTargetEnqueuer,
    resolveTargetEnqueuer: ResolveTargetEnqueuer,
) : AbstractProcessor<OparlLegislativeTermDto, RawLegislativeTerm>(crawlTargetEnqueuer, resolveTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawLegislativeTerm? =
        rawLegislativeTermRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawLegislativeTerm = RawLegislativeTerm(resource)

    override fun saveEntity(entity: RawLegislativeTerm) = rawLegislativeTermRepository.save(entity)

    override fun process(resource: OparlResource, dto: OparlLegislativeTermDto): RawLegislativeTerm {
        enqueueUrls(resource, CrawlEntityType.BODY, dto.body)

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
