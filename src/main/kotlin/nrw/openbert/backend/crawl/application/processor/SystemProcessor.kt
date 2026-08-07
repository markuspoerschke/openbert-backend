package nrw.openbert.backend.crawl.application.processor

import nrw.openbert.backend.crawl.application.CrawlTargetEnqueuer
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlSystemDto
import nrw.openbert.backend.resolve.application.ResolveTargetEnqueuer
import nrw.openbert.backend.oparl.domain.entity.AbstractOparlEntity
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawSystem
import nrw.openbert.backend.oparl.domain.repository.raw.RawSystemRepository
import org.springframework.stereotype.Service

@Service
class SystemProcessor(
    val rawSystemRepository: RawSystemRepository,
    crawlTargetEnqueuer: CrawlTargetEnqueuer,
    resolveTargetEnqueuer: ResolveTargetEnqueuer,
) : AbstractProcessor<OparlSystemDto, RawSystem>(crawlTargetEnqueuer, resolveTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawSystem? = rawSystemRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawSystem = RawSystem(resource)

    override fun saveEntity(entity: RawSystem) = rawSystemRepository.save(entity)

    override fun process(resource: OparlResource, dto: OparlSystemDto): RawSystem {
        enqueueUrls(resource, dto.body)

        return super.process(resource, dto)
    }

    override fun updateEntity(entity: RawSystem, dto: OparlSystemDto) {
        entity.oparlVersion = dto.oparlVersion ?: AbstractOparlEntity.UNKNOWN
        entity.otherOparlVersions = dto.otherOparlVersions.orEmpty().toMutableList()
        entity.license = dto.license?.toOparlUrl()
        entity.name = dto.name ?: AbstractOparlEntity.UNKNOWN
        entity.contactEmail = dto.contactEmail
        entity.contactName = dto.contactName
        entity.website = dto.website?.toOparlUrl()
        entity.vendor = dto.vendor?.toOparlUrl()
        entity.product = dto.product?.toOparlUrl()
        entity.web = dto.web
    }
}
