package nrw.openbert.backend.parldata.adapter.inbound.crawler.processor

import nrw.openbert.backend.parldata.application.crawl.CrawlTargetEnqueuer
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlFileDto
import nrw.openbert.backend.parldata.application.enrich.EnrichTargetEnqueuer
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawFile
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawFileRepository
import org.springframework.stereotype.Service

@Service
class FileProcessor(
    val rawFileRepository: RawFileRepository,
    crawlTargetEnqueuer: CrawlTargetEnqueuer,
    enrichTargetEnqueuer: EnrichTargetEnqueuer,
) : AbstractProcessor<OparlFileDto, RawFile>(crawlTargetEnqueuer, enrichTargetEnqueuer) {
    override fun findEntity(resource: OparlResource): RawFile? = rawFileRepository.findByResource(resource)

    override fun createEntity(resource: OparlResource): RawFile = RawFile(resource)

    override fun saveEntity(entity: RawFile) = rawFileRepository.save(entity)

    override fun process(resource: OparlResource, dto: OparlFileDto): RawFile {
        enqueueUrls(resource, OparlType.FILE, dto.masterFile)
        enqueueUrls(resource, OparlType.FILE, dto.derivativeFile.orEmpty())
        enqueueUrls(resource, OparlType.MEETING, dto.meeting.orEmpty())
        enqueueUrls(resource, OparlType.AGENDA_ITEM, dto.agendaItem.orEmpty())
        enqueueUrls(resource, OparlType.PAPER, dto.paper.orEmpty())

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
