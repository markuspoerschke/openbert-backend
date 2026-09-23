package nrw.openbert.backend.parldata.adapter.inbound.crawler.processor

import nrw.openbert.backend.parldata.adapter.inbound.crawler.CrawlTargetEnqueuer
import nrw.openbert.backend.parldata.domain.enums.CrawlEntityType
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlBodyDto
import nrw.openbert.backend.parldata.application.resolve.ResolveTargetEnqueuer
import nrw.openbert.backend.parldata.domain.entity.AbstractOparlEntity
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawBody
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawBodyRepository
import org.springframework.stereotype.Service

@Service
class BodyProcessor(
    val rawBodyRepository: RawBodyRepository,
    crawlTargetEnqueuer: CrawlTargetEnqueuer,
    private val legislativeTermProcessor: LegislativeTermProcessor,
    private val locationProcessor: LocationProcessor,
    resolveTargetEnqueuer: ResolveTargetEnqueuer,
) : AbstractProcessor<OparlBodyDto, RawBody>(crawlTargetEnqueuer, resolveTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawBody? = rawBodyRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawBody = RawBody(resource)

    override fun saveEntity(entity: RawBody) = rawBodyRepository.save(entity)

    override fun process(resource: OparlResource, dto: OparlBodyDto): RawBody {
        enqueueUrls(resource, CrawlEntityType.SYSTEM, dto.system)
        enqueueUrls(resource, CrawlEntityType.ORGANIZATION, dto.organization)
        enqueueUrls(resource, CrawlEntityType.PERSON, dto.person)
        enqueueUrls(resource, CrawlEntityType.MEETING, dto.meeting)
        enqueueUrls(resource, CrawlEntityType.PAPER, dto.paper)
        enqueueUrls(resource, CrawlEntityType.AGENDA_ITEM, dto.agendaItem)
        enqueueUrls(resource, CrawlEntityType.CONSULTATION, dto.consultation)
        enqueueUrls(resource, CrawlEntityType.FILE, dto.file)
        enqueueUrls(resource, CrawlEntityType.LOCATION, dto.locationList)
        enqueueUrls(resource, CrawlEntityType.LEGISLATIVE_TERM, dto.legislativeTermList)
        enqueueUrls(resource, CrawlEntityType.MEMBERSHIP, dto.membership)
        enqueueUrls(resource, CrawlEntityType.BODY, dto.equivalent.orEmpty())
        processInline(resource, dto.legislativeTerm, legislativeTermProcessor)
            .forEach { it.bodyUrl = resource.url }
        processInline(resource, dto.location, locationProcessor)
            ?.bodyUrls
            ?.addUrl(resource.url)

        return super.process(resource, dto)
    }

    override fun updateEntity(entity: RawBody, dto: OparlBodyDto) {
        entity.shortName = dto.shortName
        entity.name = dto.name ?: AbstractOparlEntity.UNKNOWN
        entity.website = dto.website
        entity.license = dto.license
        entity.licenseValidSince = dto.licenseValidSince?.toOparlDateTime()
        entity.oparlSince = dto.oparlSince?.toOparlDateTime()
        entity.ags = dto.ags
        entity.rgs = dto.rgs
        entity.equivalent = dto.equivalent.orEmpty().toMutableList()
        entity.contactEmail = dto.contactEmail
        entity.contactName = dto.contactName
        entity.classification = dto.classification
        entity.keyword = dto.keyword.orEmpty().toMutableList()
        entity.web = dto.web
    }
}
