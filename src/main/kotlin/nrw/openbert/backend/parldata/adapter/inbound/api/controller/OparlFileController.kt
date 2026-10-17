package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlFileDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlFileMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.OparlClient
import nrw.openbert.backend.parldata.application.service.FileStorageService
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedFile
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedFileRepository
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
    private val oparlClient: OparlClient,
    private val fileStorageService: FileStorageService,
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

        val resolvedFile = file.get()
        val content = getFileContent(resolvedFile)

        if (content == null) {
            return ResponseEntity.notFound().build()
        }

        val mediaType = resolvedFile.mimeType?.let { MediaType.parseMediaType(it) }
            ?: MediaType.APPLICATION_OCTET_STREAM

        return ResponseEntity
            .ok()
            .contentType(mediaType)
            .contentLength(content.size.toLong())
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
        val content = getFileContent(resolvedFile)

        if (content == null) {
            return ResponseEntity.notFound().build()
        }

        val mediaType = resolvedFile.mimeType?.let { MediaType.parseMediaType(it) }
            ?: MediaType.APPLICATION_OCTET_STREAM

        return ResponseEntity
            .ok()
            .contentType(mediaType)
            .contentLength(content.size.toLong())
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

    private fun getFileContent(file: ResolvedFile): ByteArray? {
        val downloadUrl = file.downloadUrl ?: file.accessUrl ?: return null

        try {
            val fileId = file.id
            val fileName = file.fileName

            if (fileStorageService.fileExists(fileId, fileName)) {
                val cachedContent = fileStorageService.getFile(fileId, fileName)
                if (cachedContent != null && verifyChecksum(cachedContent, file)) {
                    return cachedContent
                }
            }

            val metadata = oparlClient.getFileMetadata(downloadUrl.value)
            val content = metadata.content

            if (content.isNotEmpty() && verifyChecksum(content, file)) {
                fileStorageService.storeFile(fileId, content, fileName)
                return content
            }

            return null
        } catch (e: Exception) {
            return null
        }
    }

    private fun verifyChecksum(content: ByteArray, file: ResolvedFile): Boolean {
        val sha512Checksum = file.sha512Checksum
        val sha1Checksum = file.sha1Checksum

        if (sha512Checksum != null) {
            val computedSha512 = fileStorageService.computeSha512(content)
            if (computedSha512 != sha512Checksum.lowercase()) {
                return false
            }
        }

        if (sha1Checksum != null) {
            val computedSha1 = fileStorageService.computeSha1(content)
            if (computedSha1 != sha1Checksum.lowercase()) {
                return false
            }
        }

        return true
    }
}
