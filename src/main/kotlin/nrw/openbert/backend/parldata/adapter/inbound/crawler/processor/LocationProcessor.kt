package nrw.openbert.backend.parldata.adapter.inbound.crawler.processor

import nrw.openbert.backend.parldata.application.crawl.CrawlTargetEnqueuer
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlLocationDto
import nrw.openbert.backend.parldata.application.enrich.EnrichTargetEnqueuer
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawLocation
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawLocationRepository
import org.springframework.stereotype.Service

@Service
class LocationProcessor(
    val rawLocationRepository: RawLocationRepository,
    crawlTargetEnqueuer: CrawlTargetEnqueuer,
    enrichTargetEnqueuer: EnrichTargetEnqueuer,
) : AbstractProcessor<OparlLocationDto, RawLocation>(crawlTargetEnqueuer, enrichTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawLocation? = rawLocationRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawLocation = RawLocation(resource)

    override fun saveEntity(entity: RawLocation) = rawLocationRepository.save(entity)

    override fun process(resource: OparlResource, dto: OparlLocationDto): RawLocation {
        enqueueUrls(resource, OparlType.BODY, dto.bodies.orEmpty())
        enqueueUrls(resource, OparlType.ORGANIZATION, dto.organizations.orEmpty())
        enqueueUrls(resource, OparlType.PERSON, dto.persons.orEmpty())
        enqueueUrls(resource, OparlType.MEETING, dto.meetings.orEmpty())
        enqueueUrls(resource, OparlType.PAPER, dto.papers.orEmpty())

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
