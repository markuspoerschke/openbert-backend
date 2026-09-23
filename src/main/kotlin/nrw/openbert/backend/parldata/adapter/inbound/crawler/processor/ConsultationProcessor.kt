package nrw.openbert.backend.parldata.adapter.inbound.crawler.processor

import nrw.openbert.backend.parldata.adapter.inbound.crawler.CrawlTargetEnqueuer
import nrw.openbert.backend.parldata.domain.enums.CrawlEntityType
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlConsultationDto
import nrw.openbert.backend.parldata.application.resolve.ResolveTargetEnqueuer
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawConsultation
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawConsultationRepository
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
