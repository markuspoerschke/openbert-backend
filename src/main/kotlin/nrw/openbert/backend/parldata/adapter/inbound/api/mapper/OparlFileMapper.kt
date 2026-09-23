package nrw.openbert.backend.parldata.adapter.inbound.api.mapper

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlFileDto
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.domain.entity.AbstractFile
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedFile
import org.springframework.stereotype.Component
import java.time.ZoneId
import java.util.Date

@Component
class OparlFileMapper(
    private val urlGenerator: OparlUrlGenerator,
) {
    fun toDto(file: ResolvedFile): OparlFileDto {
        return OparlFileDto(
            id = urlGenerator.file(file.body.id, file.id),
            type = AbstractFile.TYPE,
            name = file.name,
            fileName = file.fileName,
            mimeType = file.mimeType,
            date = file.date?.toOparlDate(),
            size = file.size,
            sha1Checksum = file.sha1Checksum,
            sha512Checksum = file.sha512Checksum,
            text = file.text,
            accessUrl = urlGenerator.fileAccess(file.body.id, file.id),
            downloadUrl = urlGenerator.fileDownload(file.body.id, file.id),
            externalServiceUrl = file.externalServiceUrl?.value,
            fileLicense = file.fileLicense?.value,
            license = file.license,
            keyword = file.keyword.toList().takeIf { it.isNotEmpty() },
            created = file.created.toString(),
            modified = file.modified.toString(),
            web = file.web,
            deleted = file.deleted,
        )
    }

    private fun Date.toOparlDate(): String {
        return toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString()
    }
}
