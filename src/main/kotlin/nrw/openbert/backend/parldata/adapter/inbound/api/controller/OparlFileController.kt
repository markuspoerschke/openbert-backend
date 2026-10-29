package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlFileDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlFileMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.model.FileContent
import nrw.openbert.backend.parldata.application.port.outbound.FileContentFetcher
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedFileRepository
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedFile
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.UUID

@Tag(
    name = "Oparl File",
    externalDocs =
        ExternalDocumentation(
            "Oparl Specs",
            "https://oparl.org/spezifikation/online-ansicht/#entity-file",
        ),
)
@RestController
class OparlFileController(
    private val urlGenerator: OparlUrlGenerator,
    private val fileMapper: OparlFileMapper,
    private val listPaginationMapper: OparlListPaginationMapper,
    private val listLinksMapper: OparlListLinksMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedFileRepository: ResolvedFileRepository,
    private val fileContentFetcher: FileContentFetcher,
) {
    @GetMapping("oparl/v1.1/body/{bodyId}/file")
    fun list(
        @PathVariable bodyId: UUID,
        @ParameterObject @Valid pagination: OparlPaginationRequestDto,
    ): ResponseEntity<OparlListDto<OparlFileDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val files =
            resolvedFileRepository
                .findAllByBodyIdOrderById(bodyId, pagination.toPageable())
                .map(fileMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.file.list(bodyId, pagination.page),
                data = files.toList(),
                pagination = listPaginationMapper.toDto(files),
                links =
                    listLinksMapper.toDto(
                        files,
                        { page -> urlGenerator.file.list(bodyId, page) },
                    ),
            ),
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
        response: HttpServletResponse,
    ) {
        serveFile(bodyId, fileId, response, attachment = false)
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/file/{fileId}/download")
    fun download(
        @PathVariable bodyId: UUID,
        @PathVariable fileId: UUID,
        response: HttpServletResponse,
    ) {
        serveFile(bodyId, fileId, response, attachment = true)
    }

    private fun serveFile(
        bodyId: UUID,
        fileId: UUID,
        response: HttpServletResponse,
        attachment: Boolean,
    ) {
        val file = resolvedFileRepository.findByIdAndBodyId(fileId, bodyId).orElse(null)
        val url =
            if (attachment) {
                file?.downloadUrl ?: file?.accessUrl
            } else {
                file?.accessUrl ?: file?.downloadUrl
            }

        if (file == null || url == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND)
            return
        }

        try {
            fileContentFetcher.fetch(url) { content ->
                writeContent(content, file, response, attachment)
            }
        } catch (exception: IOException) {
            if (response.isCommitted) {
                throw exception
            }
            response.reset()
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY)
        }
    }

    private fun writeContent(
        content: FileContent,
        file: ResolvedFile,
        response: HttpServletResponse,
        attachment: Boolean,
    ) {
        if (!HttpStatusCode.valueOf(content.statusCode).is2xxSuccessful) {
            val status =
                when (content.statusCode) {
                    HttpServletResponse.SC_NOT_FOUND, HttpServletResponse.SC_GONE -> content.statusCode
                    else -> HttpServletResponse.SC_BAD_GATEWAY
                }
            response.sendError(status)
            return
        }

        response.status = content.statusCode
        response.contentType = content.contentType ?: file.mimeType ?: MediaType.APPLICATION_OCTET_STREAM_VALUE
        content.contentLength?.let(response::setContentLengthLong)
        content.contentEncoding?.let { response.setHeader(HttpHeaders.CONTENT_ENCODING, it) }
        if (attachment) {
            response.setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition
                    .attachment()
                    .filename(file.fileName ?: "file-${file.id}", StandardCharsets.UTF_8)
                    .build()
                    .toString(),
            )
        }
        content.body.copyTo(response.outputStream)
    }
}
