package nrw.openbert.backend.parldata.adapter.inbound.crawler.processor

import nrw.openbert.backend.parldata.application.crawl.CrawlTargetEnqueuer
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlConsultationDto
import nrw.openbert.backend.parldata.application.enrich.EnrichTargetEnqueuer
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawConsultation
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawConsultationRepository
import org.springframework.stereotype.Service

@Service
class ConsultationProcessor(
    val rawConsultationRepository: RawConsultationRepository,
    crawlTargetEnqueuer: CrawlTargetEnqueuer,
    enrichTargetEnqueuer: EnrichTargetEnqueuer,
) : AbstractProcessor<OparlConsultationDto, RawConsultation>(crawlTargetEnqueuer, enrichTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawConsultation? =
        rawConsultationRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawConsultation = RawConsultation(resource)

    override fun saveEntity(entity: RawConsultation) = rawConsultationRepository.save(entity)

    override fun processRelationships(resource: OparlResource, dto: OparlConsultationDto) {
        enqueueUrls(resource, OparlType.PAPER, dto.paper)
        enqueueUrls(resource, OparlType.AGENDA_ITEM, dto.agendaItem)
        enqueueUrls(resource, OparlType.MEETING, dto.meeting)
        enqueueUrls(resource, OparlType.ORGANIZATION, dto.organization.orEmpty())
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
