package nrw.openbert.backend.crawl.application.processor

import nrw.openbert.backend.crawl.application.CrawlTargetEnqueuer
import nrw.openbert.backend.crawl.domain.entity.CrawlEntityType
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlPaperDto
import nrw.openbert.backend.resolve.application.ResolveTargetEnqueuer
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawPaper
import nrw.openbert.backend.oparl.domain.repository.raw.RawPaperRepository
import org.springframework.stereotype.Service

@Service
class PaperProcessor(
    val rawPaperRepository: RawPaperRepository,
    crawlTargetEnqueuer: CrawlTargetEnqueuer,
    private val fileProcessor: FileProcessor,
    private val locationProcessor: LocationProcessor,
    private val consultationProcessor: ConsultationProcessor,
    resolveTargetEnqueuer: ResolveTargetEnqueuer,
) : AbstractProcessor<OparlPaperDto, RawPaper>(crawlTargetEnqueuer, resolveTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawPaper? = rawPaperRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawPaper = RawPaper(resource)

    override fun saveEntity(entity: RawPaper) = rawPaperRepository.save(entity)

    override fun process(resource: OparlResource, dto: OparlPaperDto): RawPaper {
        enqueueUrls(resource, CrawlEntityType.BODY, dto.body)
        enqueueUrls(resource, CrawlEntityType.PAPER, dto.relatedPaper.orEmpty())
        enqueueUrls(resource, CrawlEntityType.PAPER, dto.superordinatedPaper.orEmpty())
        enqueueUrls(resource, CrawlEntityType.PAPER, dto.subordinatedPaper.orEmpty())
        enqueueUrls(resource, CrawlEntityType.PERSON, dto.originatorPerson.orEmpty())
        enqueueUrls(resource, CrawlEntityType.ORGANIZATION, dto.underDirectionOf.orEmpty())
        enqueueUrls(resource, CrawlEntityType.ORGANIZATION, dto.originatorOrganization.orEmpty())
        processInline(resource, dto.mainFile, fileProcessor)
            ?.paperUrls
            ?.addUrl(resource.url)
        processInline(resource, dto.auxiliaryFile, fileProcessor)
            .forEach { it.paperUrls.addUrl(resource.url) }
        processInline(resource, dto.location, locationProcessor)
            .forEach { it.paperUrls.addUrl(resource.url) }
        processInline(resource, dto.consultation, consultationProcessor)
            .forEach { it.paperUrl = resource.url }

        return super.process(resource, dto)
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
