package nrw.openbert.backend.oparl.infrastructure.api.v1.controller

import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedFile
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlFileDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlListDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.mapper.OparlFileMapper
import nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedBodyRepository
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedFileRepository
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.StandardCharsets
import java.util.UUID

@RestController
class OparlFileController(
    private val urlGenerator: OparlUrlGenerator,
    private val fileMapper: OparlFileMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedFileRepository: ResolvedFileRepository,
) {
    @GetMapping("oparl/v1.1/body/{bodyId}/file")
    fun list(@PathVariable bodyId: UUID): ResponseEntity<OparlListDto<OparlFileDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val files = resolvedFileRepository
            .findAllByBody_IdOrderById(bodyId)
            .map(fileMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.fileList(bodyId),
                data = files,
            )
        )
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/file/{fileId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable fileId: UUID,
    ): ResponseEntity<OparlFileDto> {
        val file = resolvedFileRepository.findByIdAndBody_Id(fileId, bodyId)

        if (!file.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(fileMapper.toDto(file.get()))
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/file/{fileId}/access")
    fun access(
        @PathVariable bodyId: UUID,
        @PathVariable fileId: UUID,
    ): ResponseEntity<ByteArray> {
        val file = resolvedFileRepository.findByIdAndBody_Id(fileId, bodyId)

        if (!file.isPresent) {
            return ResponseEntity.notFound().build()
        }

        val content = placeholderContent(file.get())

        return ResponseEntity
            .ok()
            .contentType(MediaType.TEXT_PLAIN)
            .contentLength(content.size.toLong())
            .header(MOCK_FILE_HEADER, "true")
            .body(content)
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/file/{fileId}/download")
    fun download(
        @PathVariable bodyId: UUID,
        @PathVariable fileId: UUID,
    ): ResponseEntity<ByteArray> {
        val file = resolvedFileRepository.findByIdAndBody_Id(fileId, bodyId)

        if (!file.isPresent) {
            return ResponseEntity.notFound().build()
        }

        val resolvedFile = file.get()
        val fileName = resolvedFile.fileName ?: "file-$fileId.txt"
        val content = placeholderContent(resolvedFile)

        return ResponseEntity
            .ok()
            .contentType(MediaType.TEXT_PLAIN)
            .contentLength(content.size.toLong())
            .header(MOCK_FILE_HEADER, "true")
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition
                    .attachment()
                    .filename(fileName, StandardCharsets.UTF_8)
                    .build()
                    .toString(),
            )
            .body(content)
    }

    private fun placeholderContent(file: ResolvedFile): ByteArray {
        return (
            "Placeholder for ${file.fileName ?: "file-${file.id}"}.\n" +
                "The original file content is not available.\n"
            ).toByteArray(StandardCharsets.UTF_8)
    }

    companion object {
        private const val MOCK_FILE_HEADER = "X-OpenBERT-Mock-File"
    }
}
