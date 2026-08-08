package nrw.openbert.backend.crawl.application.processor

import nrw.openbert.backend.crawl.application.CrawlTargetEnqueuer
import nrw.openbert.backend.crawl.domain.entity.CrawlEntityType
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlLocationDto
import nrw.openbert.backend.resolve.application.ResolveTargetEnqueuer
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawLocation
import nrw.openbert.backend.oparl.domain.repository.raw.RawLocationRepository
import org.springframework.stereotype.Service

@Service
class LocationProcessor(
    val rawLocationRepository: RawLocationRepository,
    crawlTargetEnqueuer: CrawlTargetEnqueuer,
    resolveTargetEnqueuer: ResolveTargetEnqueuer,
) : AbstractProcessor<OparlLocationDto, RawLocation>(crawlTargetEnqueuer, resolveTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawLocation? = rawLocationRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawLocation = RawLocation(resource)

    override fun saveEntity(entity: RawLocation) = rawLocationRepository.save(entity)

    override fun process(resource: OparlResource, dto: OparlLocationDto): RawLocation {
        enqueueUrls(resource, CrawlEntityType.BODY, dto.bodies.orEmpty())
        enqueueUrls(resource, CrawlEntityType.ORGANIZATION, dto.organizations.orEmpty())
        enqueueUrls(resource, CrawlEntityType.PERSON, dto.persons.orEmpty())
        enqueueUrls(resource, CrawlEntityType.MEETING, dto.meetings.orEmpty())
        enqueueUrls(resource, CrawlEntityType.PAPER, dto.papers.orEmpty())

        return super.process(resource, dto)
    }

    override fun updateEntity(entity: RawLocation, dto: OparlLocationDto) {
        entity.bodyUrls = dto.bodies.orEmpty().toMutableList()
        entity.organizationUrls = dto.organizations.orEmpty().toMutableList()
        entity.personUrls = dto.persons.orEmpty().toMutableList()
        entity.meetingUrls = dto.meetings.orEmpty().toMutableList()
        entity.paperUrls = dto.papers.orEmpty().toMutableList()
        entity.description = dto.description
        entity.geojson = dto.geojson
        entity.streetAddress = dto.streetAddress
        entity.room = dto.room
        entity.postalCode = dto.postalCode
        entity.subLocality = dto.subLocality
        entity.locality = dto.locality
        entity.license = dto.license
        entity.keyword = dto.keyword.orEmpty().toMutableList()
        entity.web = dto.web
    }
}
