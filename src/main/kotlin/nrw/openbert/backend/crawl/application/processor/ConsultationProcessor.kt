package nrw.openbert.backend.crawl.application.processor

import nrw.openbert.backend.crawl.application.CrawlTargetEnqueuer
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlConsultationDto
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawConsultation
import nrw.openbert.backend.oparl.domain.repository.raw.RawConsultationRepository
import org.springframework.stereotype.Service

@Service
class ConsultationProcessor(
    val rawConsultationRepository: RawConsultationRepository,
    crawlTargetEnqueuer: CrawlTargetEnqueuer,
) : AbstractProcessor<OparlConsultationDto, RawConsultation>(crawlTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawConsultation? =
        rawConsultationRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawConsultation = RawConsultation(resource)

    override fun saveEntity(entity: RawConsultation) = rawConsultationRepository.save(entity)

    override fun process(resource: OparlResource, dto: OparlConsultationDto): RawConsultation {
        enqueueUrls(resource, dto.paper, dto.agendaItem, dto.meeting)
        enqueueUrls(resource, dto.organization.orEmpty())

        return super.process(resource, dto)
    }

    override fun updateEntity(entity: RawConsultation, dto: OparlConsultationDto) {
        entity.authoritative = dto.authoritative
        entity.role = dto.role
        entity.license = dto.license
        entity.keyword = dto.keyword.orEmpty().toMutableList()
        entity.web = dto.web
    }
}
