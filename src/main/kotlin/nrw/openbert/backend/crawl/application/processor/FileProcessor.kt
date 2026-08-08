package nrw.openbert.backend.crawl.application.processor

import nrw.openbert.backend.crawl.application.CrawlTargetEnqueuer
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlFileDto
import nrw.openbert.backend.resolve.application.ResolveTargetEnqueuer
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawFile
import nrw.openbert.backend.oparl.domain.repository.raw.RawFileRepository
import org.springframework.stereotype.Service

@Service
class FileProcessor(
    val rawFileRepository: RawFileRepository,
    crawlTargetEnqueuer: CrawlTargetEnqueuer,
    resolveTargetEnqueuer: ResolveTargetEnqueuer,
) : AbstractProcessor<OparlFileDto, RawFile>(crawlTargetEnqueuer, resolveTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawFile? = rawFileRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawFile = RawFile(resource)

    override fun saveEntity(entity: RawFile) = rawFileRepository.save(entity)

    override fun process(resource: OparlResource, dto: OparlFileDto): RawFile {
        enqueueUrls(resource, dto.masterFile)
        enqueueUrls(resource, dto.derivativeFile.orEmpty())
        enqueueUrls(resource, dto.meeting.orEmpty())
        enqueueUrls(resource, dto.agendaItem.orEmpty())
        enqueueUrls(resource, dto.paper.orEmpty())

        return super.process(resource, dto)
    }

    override fun updateEntity(entity: RawFile, dto: OparlFileDto) {
        entity.meetingUrls = dto.meeting.orEmpty().toMutableList()
        entity.agendaItemUrls = dto.agendaItem.orEmpty().toMutableList()
        entity.paperUrls = dto.paper.orEmpty().toMutableList()
        entity.name = dto.name
        entity.fileName = dto.fileName
        entity.mimeType = dto.mimeType
        entity.date = dto.date?.toOparlDate()
        entity.size = dto.size
        entity.sha1Checksum = dto.sha1Checksum
        entity.sha512Checksum = dto.sha512Checksum
        entity.text = dto.text
        entity.accessUrl = dto.accessUrl?.toOparlUrl()
        entity.downloadUrl = dto.downloadUrl?.toOparlUrl()
        entity.externalServiceUrl = dto.externalServiceUrl?.toOparlUrl()
        entity.fileLicense = dto.fileLicense?.toOparlUrl()
        entity.license = dto.license
        entity.keyword = dto.keyword.orEmpty().toMutableList()
        entity.web = dto.web
    }
}
