package nrw.openbert.backend.parldata.adapter.inbound.crawler.processor

import nrw.openbert.backend.parldata.application.crawl.CrawlTargetEnqueuer
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlOrganizationDto
import nrw.openbert.backend.parldata.application.resolve.ResolveTargetEnqueuer
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawOrganization
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawOrganizationRepository
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
        enqueueUrls(resource, OparlType.BODY, dto.body, dto.externalBody)
        enqueueUrls(resource, OparlType.MEETING, dto.meeting)
        enqueueUrls(resource, OparlType.CONSULTATION, dto.consultation)
        enqueueUrls(resource, OparlType.ORGANIZATION, dto.subOrganizationOf)
        enqueueUrls(resource, OparlType.MEMBERSHIP, dto.membership.orEmpty())
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
