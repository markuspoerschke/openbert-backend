package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import jakarta.validation.Valid
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlFileDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlFileMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedFile
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedFileRepository
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springdoc.core.annotations.ParameterObject
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
    private val listPaginationMapper: OparlListPaginationMapper,
    private val listLinksMapper: OparlListLinksMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedFileRepository: ResolvedFileRepository,
) {
    @GetMapping("oparl/v1.1/body/{bodyId}/file")
    fun list(
        @PathVariable bodyId: UUID,
        @ParameterObject @Valid pagination: OparlPaginationRequestDto,
    ): ResponseEntity<OparlListDto<OparlFileDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val files = resolvedFileRepository
            .findAllByBodyIdOrderById(bodyId, pagination.toPageable())
            .map(fileMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.file.list(bodyId, pagination.page),
                data = files.toList(),
                pagination = listPaginationMapper.toDto(files),
                links = listLinksMapper.toDto(
                    files,
                    { page -> urlGenerator.file.list(bodyId, page) }
                ),
            )
        )
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/file/{fileId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable fileId: UUID,
    ): ResponseEntity<OparlFileDto> {
        val file = resolvedFileRepository.findByIdAndBodyId(fileId, bodyId)

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
        val file = resolvedFileRepository.findByIdAndBodyId(fileId, bodyId)

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
        val file = resolvedFileRepository.findByIdAndBodyId(fileId, bodyId)

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
