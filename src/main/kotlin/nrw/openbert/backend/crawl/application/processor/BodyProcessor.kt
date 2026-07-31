package nrw.openbert.backend.crawl.application.processor

import nrw.openbert.backend.crawl.application.CrawlTargetEnqueuer
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlBodyDto
import nrw.openbert.backend.oparl.domain.entity.AbstractOparlEntity
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawBody
import nrw.openbert.backend.oparl.domain.repository.raw.RawBodyRepository
import org.springframework.stereotype.Service

@Service
class BodyProcessor(
    val rawBodyRepository: RawBodyRepository,
    crawlTargetEnqueuer: CrawlTargetEnqueuer,
    private val legislativeTermProcessor: LegislativeTermProcessor,
    private val locationProcessor: LocationProcessor,
) : AbstractProcessor<OparlBodyDto, RawBody>(crawlTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawBody? = rawBodyRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawBody = RawBody(resource)

    override fun saveEntity(entity: RawBody) = rawBodyRepository.save(entity)

    override fun process(resource: OparlResource, dto: OparlBodyDto): RawBody {
        enqueueUrls(
            resource,
            dto.system,
            dto.organization,
            dto.person,
            dto.meeting,
            dto.paper,
            dto.agendaItem,
            dto.consultation,
            dto.file,
            dto.locationList,
            dto.legislativeTermList,
            dto.membership,
        )
        enqueueUrls(resource, dto.equivalent.orEmpty())
        processInline(resource, dto.legislativeTerm, legislativeTermProcessor)
        processInline(resource, dto.location, locationProcessor)

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
