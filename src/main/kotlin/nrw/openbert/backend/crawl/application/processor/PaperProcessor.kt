package nrw.openbert.backend.crawl.application.processor

import nrw.openbert.backend.crawl.application.CrawlTargetEnqueuer
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlPaperDto
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
) : AbstractProcessor<OparlPaperDto, RawPaper>(crawlTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawPaper? = rawPaperRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawPaper = RawPaper(resource)

    override fun saveEntity(entity: RawPaper) = rawPaperRepository.save(entity)

    override fun process(resource: OparlResource, dto: OparlPaperDto): RawPaper {
        enqueueUrls(resource, dto.body)
        enqueueUrls(resource, dto.relatedPaper.orEmpty())
        enqueueUrls(resource, dto.superordinatedPaper.orEmpty())
        enqueueUrls(resource, dto.subordinatedPaper.orEmpty())
        enqueueUrls(resource, dto.originatorPerson.orEmpty())
        enqueueUrls(resource, dto.underDirectionOf.orEmpty())
        enqueueUrls(resource, dto.originatorOrganization.orEmpty())
        processInline(resource, dto.mainFile, fileProcessor)
        processInline(resource, dto.auxiliaryFile, fileProcessor)
        processInline(resource, dto.location, locationProcessor)
        processInline(resource, dto.consultation, consultationProcessor)

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
