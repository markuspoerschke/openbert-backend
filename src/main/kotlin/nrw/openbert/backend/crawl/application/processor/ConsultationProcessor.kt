package nrw.openbert.backend.crawl.application.processor

import nrw.openbert.backend.crawl.application.CrawlTargetEnqueuer
import nrw.openbert.backend.crawl.domain.entity.CrawlEntityType
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlConsultationDto
import nrw.openbert.backend.resolve.application.ResolveTargetEnqueuer
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawConsultation
import nrw.openbert.backend.oparl.domain.repository.raw.RawConsultationRepository
import org.springframework.stereotype.Service

@Service
class ConsultationProcessor(
    val rawConsultationRepository: RawConsultationRepository,
    crawlTargetEnqueuer: CrawlTargetEnqueuer,
    resolveTargetEnqueuer: ResolveTargetEnqueuer,
) : AbstractProcessor<OparlConsultationDto, RawConsultation>(crawlTargetEnqueuer, resolveTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawConsultation? =
        rawConsultationRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawConsultation = RawConsultation(resource)

    override fun saveEntity(entity: RawConsultation) = rawConsultationRepository.save(entity)

    override fun process(resource: OparlResource, dto: OparlConsultationDto): RawConsultation {
        enqueueUrls(resource, CrawlEntityType.PAPER, dto.paper)
        enqueueUrls(resource, CrawlEntityType.AGENDA_ITEM, dto.agendaItem)
        enqueueUrls(resource, CrawlEntityType.MEETING, dto.meeting)
        enqueueUrls(resource, CrawlEntityType.ORGANIZATION, dto.organization.orEmpty())

        return super.process(resource, dto)
    }

    override fun updateEntity(entity: RawConsultation, dto: OparlConsultationDto) {
        entity.paperUrl = dto.paper?.toOparlUrl()
        entity.agendaItemUrl = dto.agendaItem?.toOparlUrl()
        entity.meetingUrl = dto.meeting?.toOparlUrl()
        entity.organizationUrls = dto.organization.orEmpty().toMutableList()
        entity.authoritative = dto.authoritative
        entity.role = dto.role
        entity.license = dto.license
        entity.keyword = dto.keyword.orEmpty().toMutableList()
        entity.web = dto.web
    }
}
