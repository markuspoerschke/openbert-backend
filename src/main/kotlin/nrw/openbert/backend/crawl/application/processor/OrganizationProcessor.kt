package nrw.openbert.backend.crawl.application.processor

import nrw.openbert.backend.crawl.application.CrawlTargetEnqueuer
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlOrganizationDto
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawOrganization
import nrw.openbert.backend.oparl.domain.repository.raw.RawOrganizationRepository
import org.springframework.stereotype.Service

@Service
class OrganizationProcessor(
    val rawOrganizationRepository: RawOrganizationRepository,
    crawlTargetEnqueuer: CrawlTargetEnqueuer,
    private val locationProcessor: LocationProcessor,
) : AbstractProcessor<OparlOrganizationDto, RawOrganization>(crawlTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawOrganization? =
        rawOrganizationRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawOrganization = RawOrganization(resource)

    override fun saveEntity(entity: RawOrganization) = rawOrganizationRepository.save(entity)

    override fun process(resource: OparlResource, dto: OparlOrganizationDto): RawOrganization {
        enqueueUrls(
            resource,
            dto.body,
            dto.meeting,
            dto.consultation,
            dto.subOrganizationOf,
            dto.externalBody,
        )
        enqueueUrls(resource, dto.membership.orEmpty())
        processInline(resource, dto.location, locationProcessor)

        return super.process(resource, dto)
    }

    override fun updateEntity(entity: RawOrganization, dto: OparlOrganizationDto) {
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
