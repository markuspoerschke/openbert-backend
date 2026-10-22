package nrw.openbert.backend.parldata.adapter.inbound.crawler.processor

import nrw.openbert.backend.parldata.application.crawl.CrawlTargetEnqueuer
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlPersonDto
import nrw.openbert.backend.parldata.application.enrich.EnrichTargetEnqueuer
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawPerson
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawPersonRepository
import org.springframework.stereotype.Service

@Service
class PersonProcessor(
    val rawPersonRepository: RawPersonRepository,
    crawlTargetEnqueuer: CrawlTargetEnqueuer,
    private val locationProcessor: LocationProcessor,
    private val membershipProcessor: MembershipProcessor,
    enrichTargetEnqueuer: EnrichTargetEnqueuer,
) : AbstractProcessor<OparlPersonDto, RawPerson>(crawlTargetEnqueuer, enrichTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawPerson? = rawPersonRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawPerson = RawPerson(resource)

    override fun saveEntity(entity: RawPerson) = rawPersonRepository.save(entity)

    override fun processRelationships(resource: OparlResource, dto: OparlPersonDto) {
        enqueueUrls(resource, OparlType.BODY, dto.body)
        enqueueUrls(resource, OparlType.LOCATION, dto.location)
        processInline(resource, dto.locationObject, locationProcessor) { it.personUrls.addUrl(resource.url) }
        processInline(resource, dto.membership, membershipProcessor) { it.personUrl = resource.url }
    }

    override fun updateEntity(entity: RawPerson, dto: OparlPersonDto) {
        entity.bodyUrl = dto.body?.toOparlUrl()
        entity.name = dto.name
        entity.familyName = dto.familyName
        entity.givenName = dto.givenName
        entity.formOfAddress = dto.formOfAddress
        entity.affix = dto.affix
        entity.title = dto.title.orEmpty().toMutableList()
        entity.gender = dto.gender
        entity.phone = dto.phone.orEmpty().toMutableList()
        entity.email = dto.email.orEmpty().toMutableList()
        entity.status = dto.status.orEmpty().toMutableList()
        entity.life = dto.life
        entity.lifeSource = dto.lifeSource
        entity.license = dto.license
        entity.keyword = dto.keyword.orEmpty().toMutableList()
        entity.web = dto.web
    }
}
