package nrw.openbert.backend.parldata.adapter.inbound.crawler.processor

import nrw.openbert.backend.parldata.application.crawl.CrawlTargetEnqueuer
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlPaperDto
import nrw.openbert.backend.parldata.application.enrich.EnrichTargetEnqueuer
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawPaper
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawPaperRepository
import org.springframework.stereotype.Service

@Service
class PaperProcessor(
    val rawPaperRepository: RawPaperRepository,
    crawlTargetEnqueuer: CrawlTargetEnqueuer,
    private val fileProcessor: FileProcessor,
    private val locationProcessor: LocationProcessor,
    private val consultationProcessor: ConsultationProcessor,
    enrichTargetEnqueuer: EnrichTargetEnqueuer,
) : AbstractProcessor<OparlPaperDto, RawPaper>(crawlTargetEnqueuer, enrichTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawPaper? = rawPaperRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawPaper = RawPaper(resource)

    override fun saveEntity(entity: RawPaper) = rawPaperRepository.save(entity)

    override fun processRelationships(resource: OparlResource, dto: OparlPaperDto) {
        enqueueUrls(resource, OparlType.BODY, dto.body)
        enqueueUrls(resource, OparlType.PAPER, dto.relatedPaper.orEmpty())
        enqueueUrls(resource, OparlType.PAPER, dto.superordinatedPaper.orEmpty())
        enqueueUrls(resource, OparlType.PAPER, dto.subordinatedPaper.orEmpty())
        enqueueUrls(resource, OparlType.PERSON, dto.originatorPerson.orEmpty())
        enqueueUrls(resource, OparlType.ORGANIZATION, dto.underDirectionOf.orEmpty())
        enqueueUrls(resource, OparlType.ORGANIZATION, dto.originatorOrganization.orEmpty())
        processInline(resource, dto.mainFile, fileProcessor) { it.paperUrls.addUrl(resource.url) }
        processInline(resource, dto.auxiliaryFile, fileProcessor) { it.paperUrls.addUrl(resource.url) }
        processInline(resource, dto.location, locationProcessor) { it.paperUrls.addUrl(resource.url) }
        processInline(resource, dto.consultation, consultationProcessor) { it.paperUrl = resource.url }
    }

    override fun updateEntity(entity: RawPaper, dto: OparlPaperDto) {
        entity.bodyUrl = dto.body?.toOparlUrl()
        entity.name = dto.name
        entity.reference = dto.reference
        entity.date = dto.date?.toOparlDate()
        entity.paperType = dto.paperType
        entity.license = dto.license
        entity.keyword = dto.keyword.orEmpty().toMutableList()
        entity.web = dto.web
    }
}
