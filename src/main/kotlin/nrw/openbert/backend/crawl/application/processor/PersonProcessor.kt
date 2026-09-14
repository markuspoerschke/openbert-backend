package nrw.openbert.backend.crawl.application.processor

import nrw.openbert.backend.crawl.application.CrawlTargetEnqueuer
import nrw.openbert.backend.crawl.domain.entity.CrawlEntityType
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlPersonDto
import nrw.openbert.backend.resolve.application.ResolveTargetEnqueuer
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawPerson
import nrw.openbert.backend.oparl.domain.repository.raw.RawPersonRepository
import org.springframework.stereotype.Service

@Service
class PersonProcessor(
    val rawPersonRepository: RawPersonRepository,
    crawlTargetEnqueuer: CrawlTargetEnqueuer,
    private val locationProcessor: LocationProcessor,
    private val membershipProcessor: MembershipProcessor,
    resolveTargetEnqueuer: ResolveTargetEnqueuer,
) : AbstractProcessor<OparlPersonDto, RawPerson>(crawlTargetEnqueuer, resolveTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawPerson? = rawPersonRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawPerson = RawPerson(resource)

    override fun saveEntity(entity: RawPerson) = rawPersonRepository.save(entity)

    override fun process(resource: OparlResource, dto: OparlPersonDto): RawPerson {
        enqueueUrls(resource, CrawlEntityType.BODY, dto.body)
        enqueueUrls(resource, CrawlEntityType.LOCATION, dto.location)
        processInline(resource, dto.locationObject, locationProcessor)
            ?.personUrls
            ?.addUrl(resource.url)
        processInline(resource, dto.membership, membershipProcessor)
            .forEach { it.personUrl = resource.url }

        return super.process(resource, dto)
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
