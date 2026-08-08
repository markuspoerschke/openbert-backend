package nrw.openbert.backend.crawl.application.processor

import nrw.openbert.backend.crawl.application.CrawlTargetEnqueuer
import nrw.openbert.backend.crawl.domain.entity.CrawlEntityType
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlOrganizationDto
import nrw.openbert.backend.resolve.application.ResolveTargetEnqueuer
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawOrganization
import nrw.openbert.backend.oparl.domain.repository.raw.RawOrganizationRepository
import org.springframework.stereotype.Service

@Service
class OrganizationProcessor(
    val rawOrganizationRepository: RawOrganizationRepository,
    crawlTargetEnqueuer: CrawlTargetEnqueuer,
    private val locationProcessor: LocationProcessor,
    resolveTargetEnqueuer: ResolveTargetEnqueuer,
) : AbstractProcessor<OparlOrganizationDto, RawOrganization>(crawlTargetEnqueuer, resolveTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawOrganization? =
        rawOrganizationRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawOrganization = RawOrganization(resource)

    override fun saveEntity(entity: RawOrganization) = rawOrganizationRepository.save(entity)

    override fun process(resource: OparlResource, dto: OparlOrganizationDto): RawOrganization {
        enqueueUrls(resource, CrawlEntityType.BODY, dto.body, dto.externalBody)
        enqueueUrls(resource, CrawlEntityType.MEETING, dto.meeting)
        enqueueUrls(resource, CrawlEntityType.CONSULTATION, dto.consultation)
        enqueueUrls(resource, CrawlEntityType.ORGANIZATION, dto.subOrganizationOf)
        enqueueUrls(resource, CrawlEntityType.MEMBERSHIP, dto.membership.orEmpty())
        processInline(resource, dto.location, locationProcessor)
            ?.organizationUrls
            ?.addUrl(resource.url)

        return super.process(resource, dto)
    }

    override fun updateEntity(entity: RawOrganization, dto: OparlOrganizationDto) {
        entity.bodyUrl = dto.body?.toOparlUrl()
        entity.name = dto.name
        entity.shortName = dto.shortName
        entity.post = dto.post.orEmpty().toMutableList()
        entity.organizationType = dto.organizationType
        entity.classification = dto.classification
        entity.startDate = dto.startDate?.toOparlDate()
        entity.endDate = dto.endDate?.toOparlDate()
        entity.website = dto.website?.toOparlUrl()
        entity.license = dto.license
        entity.keyword = dto.keyword.orEmpty().toMutableList()
        entity.web = dto.web
    }
}
