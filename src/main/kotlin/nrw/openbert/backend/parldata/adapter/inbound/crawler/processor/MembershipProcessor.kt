package nrw.openbert.backend.parldata.adapter.inbound.crawler.processor

import nrw.openbert.backend.parldata.adapter.inbound.crawler.CrawlTargetEnqueuer
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlMembershipDto
import nrw.openbert.backend.parldata.application.resolve.ResolveTargetEnqueuer
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawMembership
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawMembershipRepository
import org.springframework.stereotype.Service

@Service
class MembershipProcessor(
    val rawMembershipRepository: RawMembershipRepository,
    crawlTargetEnqueuer: CrawlTargetEnqueuer,
    resolveTargetEnqueuer: ResolveTargetEnqueuer,
) : AbstractProcessor<OparlMembershipDto, RawMembership>(crawlTargetEnqueuer, resolveTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawMembership? =
        rawMembershipRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawMembership = RawMembership(resource)

    override fun saveEntity(entity: RawMembership) = rawMembershipRepository.save(entity)

    override fun process(resource: OparlResource, dto: OparlMembershipDto): RawMembership {
        enqueueUrls(resource, OparlType.PERSON, dto.person)
        enqueueUrls(resource, OparlType.ORGANIZATION, dto.organization, dto.onBehalfOf)

        return super.process(resource, dto)
    }

    override fun updateEntity(entity: RawMembership, dto: OparlMembershipDto) {
        entity.personUrl = dto.person?.toOparlUrl()
        entity.organizationUrl = dto.organization?.toOparlUrl()
        entity.onBehalfOfUrl = dto.onBehalfOf?.toOparlUrl()
        entity.role = dto.role
        entity.votingRight = dto.votingRight
        entity.startDate = dto.startDate?.toOparlDate()
        entity.endDate = dto.endDate?.toOparlDate()
        entity.license = dto.license
        entity.keyword = dto.keyword.orEmpty().toMutableList()
        entity.web = dto.web
    }
}
