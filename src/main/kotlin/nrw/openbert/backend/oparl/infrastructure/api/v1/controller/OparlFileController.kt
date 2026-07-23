package nrw.openbert.backend.oparl.infrastructure.api.v1.controller

import nrw.openbert.backend.oparl.domain.entity.AbstractFile
import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedFile
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlFileDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlListDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.oparl.infrastructure.persitence.repository.ResolvedBodyRepository
import nrw.openbert.backend.oparl.infrastructure.persitence.repository.ResolvedFileRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.time.ZoneId
import java.util.Date
import java.util.UUID

@RestController
class OparlFileController(
    private val urlGenerator: OparlUrlGenerator,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedFileRepository: ResolvedFileRepository,
) {
    @GetMapping("oparl/v1.1/body/{bodyId}/file")
    fun list(@PathVariable bodyId: UUID): ResponseEntity<OparlListDto<OparlFileDto>> {
        val body = resolvedBodyRepository.findById(bodyId)
        if (body.isEmpty) return ResponseEntity.notFound().build()

        val files = resolvedFileRepository
            .findAllByResource_System_IdOrderById(body.get().resource.system.id)
            .map { it.toDto() }

        return ResponseEntity.ok(OparlListDto(urlGenerator.fileList(bodyId), files))
    }

    @GetMapping("oparl/v1.1/file/{id}")
    fun get(@PathVariable id: UUID): ResponseEntity<OparlFileDto> {
        val file = resolvedFileRepository.findById(id)
        if (file.isEmpty) return ResponseEntity.notFound().build()

        return ResponseEntity.ok(file.get().toDto())
    }

    private fun ResolvedFile.toDto() = OparlFileDto(
        id = urlGenerator.file(id),
        type = AbstractFile.TYPE,
        name = name,
        fileName = fileName,
        mimeType = mimeType,
        date = date?.toOparlDate(),
        size = size,
        sha1Checksum = sha1Checksum,
        sha512Checksum = sha512Checksum,
        text = text,
        accessUrl = accessUrl?.value,
        downloadUrl = downloadUrl?.value,
        externalServiceUrl = externalServiceUrl?.value,
        fileLicense = fileLicense?.value,
        license = license,
        keyword = keyword.toList().takeIf { it.isNotEmpty() },
        created = created.toString(),
        modified = modified.toString(),
        web = web,
        deleted = deleted,
    )

    private fun Date.toOparlDate(): String =
        toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString()
}
