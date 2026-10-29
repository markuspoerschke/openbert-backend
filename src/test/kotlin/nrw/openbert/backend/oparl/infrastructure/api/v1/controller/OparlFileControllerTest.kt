package nrw.openbert.backend.oparl.infrastructure.api.v1.controller

import nrw.openbert.backend.TestcontainersConfiguration
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.OparlFileContentClient
import nrw.openbert.backend.parldata.application.port.model.FileContent
import nrw.openbert.backend.parldata.application.port.outbound.FileContentFetcher
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.mock.http.client.MockClientHttpResponse
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withException
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.GZIPOutputStream

@Import(TestcontainersConfiguration::class, OparlFileControllerTest.UpstreamConfiguration::class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OparlFileControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val upstream: ControlledUpstream,
    @Autowired private val jdbcTemplate: JdbcTemplate,
) {
    private val bodyUrl =
        "https://example.org:8443/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3"
    private val fileUrl = "$bodyUrl/file/3d663f77-05ca-4a14-b788-68ad340ea271"
    private val upstreamAccessUrl = "https://oparl-demo.example.org/files/1"
    private val upstreamDownloadUrl = "https://oparl-demo.example.org/files/download/1"
    private val fileBytes = byteArrayOf(0x25, 0x50, 0x44, 0x46, 0, 0xFF.toByte())

    @BeforeEach
    fun resetUpstream() {
        upstream.server.reset()
    }

    @AfterEach
    fun verifyUpstream() {
        upstream.server.verify()
    }

    @Test
    fun `lists resolved files belonging to a body`() {
        mockMvc
            .get("$bodyUrl/file")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$bodyUrl/file?page=1") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/List") }
                jsonPath("$.data.length()") { value(1) }
                jsonPath("$.data[0].id") { value(fileUrl) }
                jsonPath("$.data[0].type") {
                    value("https://schema.oparl.org/1.1/File")
                }
                jsonPath("$.data[0].name") { value("Mock council document") }
                jsonPath("$.data[0].fileName") { value("mock-council-document.txt") }
                jsonPath("$.data[0].mimeType") { value("text/plain") }
                jsonPath("$.data[0].date") { value("2026-07-20") }
                jsonPath("$.data[0].size") { value(87) }
                jsonPath("$.data[0].sha512Checksum") {
                    value(
                        "3ba3e94660704f7a18c3933a0a50bc401a78ccf39654ce093c7947d2ad252b9" +
                            "a675ad7a8ce9dcd7a2b61631fda9d8605bb4853a3509886ce348517f103f4f099",
                    )
                }
                jsonPath("$.data[0].text") {
                    value("Placeholder for a council document.")
                }
                jsonPath("$.data[0].accessUrl") { value("$fileUrl/access") }
                jsonPath("$.data[0].downloadUrl") { value("$fileUrl/download") }
                jsonPath("$.data[0].fileLicense") {
                    value("https://creativecommons.org/publicdomain/zero/1.0/")
                }
                jsonPath("$.data[0].license") { value("CC0-1.0") }
                jsonPath("$.data[0].keyword.length()") { value(2) }
                jsonPath("$.data[0].web") {
                    value("https://neuoffenstadt.example.com/documents/1")
                }
                jsonPath("$.pagination.currentPage") { value(1) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(1) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") { value("$bodyUrl/file?page=1") }
                jsonPath("$.links.self") { value("$bodyUrl/file?page=1") }
                jsonPath("$.links.last") { value("$bodyUrl/file?page=1") }
                jsonPath("$.links.prev") { doesNotExist() }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `paginates files and returns empty page when out of range`() {
        mockMvc
            .get("$bodyUrl/file?page=2")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$bodyUrl/file?page=2") }
                jsonPath("$.data.length()") { value(0) }
                jsonPath("$.pagination.currentPage") { value(2) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(1) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") { value("$bodyUrl/file?page=1") }
                jsonPath("$.links.self") { value("$bodyUrl/file?page=2") }
                jsonPath("$.links.prev") { value("$bodyUrl/file?page=1") }
                jsonPath("$.links.last") { value("$bodyUrl/file?page=1") }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `returns bad request when page number is less than 1`() {
        mockMvc
            .get("$bodyUrl/file?page=0")
            .andExpect {
                status { isBadRequest() }
            }

        mockMvc
            .get("$bodyUrl/file?page=-1")
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `returns a single resolved file`() {
        mockMvc
            .get(fileUrl)
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(fileUrl) }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/File") }
                jsonPath("$.accessUrl") { value("$fileUrl/access") }
                jsonPath("$.downloadUrl") { value("$fileUrl/download") }
            }
    }

    @Test
    fun `streams original file content without forcing a download`() {
        upstream.server.expect(requestTo(upstreamAccessUrl)).andRespond(
            withSuccess(fileBytes, MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_LENGTH, fileBytes.size.toString())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=upstream.pdf")
                .header(HttpHeaders.SET_COOKIE, "upstream-session=secret"),
        )

        mockMvc
            .get("$fileUrl/access")
            .andExpect {
                status { isOk() }
                content { contentType("application/pdf") }
                content { bytes(fileBytes) }
                header { longValue(HttpHeaders.CONTENT_LENGTH, fileBytes.size.toLong()) }
                header { doesNotExist(HttpHeaders.CONTENT_ENCODING) }
                header { doesNotExist(HttpHeaders.CONTENT_DISPOSITION) }
                header { doesNotExist(HttpHeaders.SET_COOKIE) }
                header { doesNotExist("X-OpenBERT-Mock-File") }
            }
    }

    @Test
    fun `streams original file content as an attachment from download url`() {
        upstream.server
            .expect(requestTo(upstreamDownloadUrl))
            .andRespond(withSuccess(fileBytes, MediaType.APPLICATION_PDF))

        mockMvc
            .get("$fileUrl/download")
            .andExpect {
                status { isOk() }
                content { contentType("application/pdf") }
                content { bytes(fileBytes) }
                header {
                    string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        containsString("attachment;"),
                    )
                }
                header {
                    string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        containsString("filename=\"mock-council-document.txt\""),
                    )
                }
                header { doesNotExist("X-OpenBERT-Mock-File") }
            }
    }

    @Test
    fun `preserves gzip encoding and compressed content length for access and download`() {
        val compressedBytes =
            ByteArrayOutputStream()
                .apply {
                    GZIPOutputStream(this).use { it.write(fileBytes) }
                }.toByteArray()
        for (url in listOf(upstreamAccessUrl, upstreamDownloadUrl)) {
            upstream.server.expect(requestTo(url)).andRespond(
                withSuccess(compressedBytes, MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_ENCODING, "gzip")
                    .header(HttpHeaders.CONTENT_LENGTH, compressedBytes.size.toString()),
            )
        }

        for (endpoint in listOf("access", "download")) {
            mockMvc.get("$fileUrl/$endpoint").andExpect {
                status { isOk() }
                content { contentType("application/pdf") }
                content { bytes(compressedBytes) }
                header { string(HttpHeaders.CONTENT_ENCODING, "gzip") }
                header { longValue(HttpHeaders.CONTENT_LENGTH, compressedBytes.size.toLong()) }
            }
        }
    }

    @Test
    fun `fetches fresh upstream content for each request`() {
        upstream.server
            .expect(requestTo(upstreamAccessUrl))
            .andRespond(withSuccess(fileBytes, MediaType.APPLICATION_PDF))
        val updatedBytes = fileBytes + byteArrayOf(1, 2, 3)
        upstream.server
            .expect(requestTo(upstreamAccessUrl))
            .andRespond(withSuccess(updatedBytes, MediaType.APPLICATION_PDF))

        mockMvc.get("$fileUrl/access").andExpect { content { bytes(fileBytes) } }
        mockMvc.get("$fileUrl/access").andExpect { content { bytes(updatedBytes) } }
    }

    @Test
    fun `access falls back to download url when access url is missing`() {
        updateFile("access_url = null")
        upstream.server
            .expect(requestTo(upstreamDownloadUrl))
            .andRespond(withSuccess(fileBytes, MediaType.APPLICATION_PDF))

        mockMvc.get("$fileUrl/access").andExpect {
            status { isOk() }
            content { bytes(fileBytes) }
            header { doesNotExist(HttpHeaders.CONTENT_DISPOSITION) }
        }
    }

    @Test
    fun `download falls back to access url and a generated filename`() {
        updateFile("download_url = null, file_name = null")
        upstream.server
            .expect(requestTo(upstreamAccessUrl))
            .andRespond(withSuccess(fileBytes, MediaType.APPLICATION_PDF))

        mockMvc.get("$fileUrl/download").andExpect {
            status { isOk() }
            content { bytes(fileBytes) }
            header {
                string(
                    HttpHeaders.CONTENT_DISPOSITION,
                    containsString("filename=\"file-3d663f77-05ca-4a14-b788-68ad340ea271\""),
                )
            }
        }
    }

    @Test
    fun `uses stored mime type and omits content length when upstream omits headers`() {
        upstream.server
            .expect(requestTo(upstreamAccessUrl))
            .andRespond(withStatus(HttpStatus.OK).body(fileBytes))

        mockMvc.get("$fileUrl/access").andExpect {
            status { isOk() }
            content { contentType("text/plain") }
            content { bytes(fileBytes) }
            header { doesNotExist(HttpHeaders.CONTENT_LENGTH) }
        }
    }

    @Test
    fun `uses binary content type when both upstream and stored mime type are missing`() {
        updateFile("mime_type = null")
        upstream.server
            .expect(requestTo(upstreamAccessUrl))
            .andRespond(withStatus(HttpStatus.OK).body(fileBytes))

        mockMvc.get("$fileUrl/access").andExpect {
            status { isOk() }
            content { contentType("application/octet-stream") }
            content { bytes(fileBytes) }
        }
    }

    @Test
    fun `returns not found when neither upstream url exists`() {
        updateFile("access_url = null, download_url = null")

        for (endpoint in listOf("access", "download")) {
            mockMvc.get("$fileUrl/$endpoint").andExpect { status { isNotFound() } }
        }
    }

    @Test
    fun `preserves upstream not found and gone but maps other failures to bad gateway`() {
        val statuses =
            listOf(
                HttpStatus.NOT_FOUND,
                HttpStatus.GONE,
                HttpStatus.FORBIDDEN,
                HttpStatus.INTERNAL_SERVER_ERROR,
            )
        for (status in statuses) {
            upstream.server
                .expect(requestTo(upstreamAccessUrl))
                .andRespond(withStatus(status).body("Upstream error details"))
        }

        for (expected in listOf(404, 410, 502, 502)) {
            mockMvc.get("$fileUrl/access").andExpect {
                status { isEqualTo(expected) }
                content { string("") }
                header { doesNotExist(HttpHeaders.CONTENT_DISPOSITION) }
            }
        }
    }

    @Test
    fun `returns bad gateway when upstream cannot be reached`() {
        upstream.server
            .expect(requestTo(upstreamDownloadUrl))
            .andRespond(withException(IOException("Connection failed")))

        mockMvc.get("$fileUrl/download").andExpect {
            status { isBadGateway() }
            header { doesNotExist(HttpHeaders.CONTENT_DISPOSITION) }
        }
    }

    @Test
    fun `clears file headers when reading upstream fails before the response is committed`() {
        upstream.server.expect(requestTo(upstreamDownloadUrl)).andRespond {
            MockClientHttpResponse(
                object : InputStream() {
                    override fun read(): Int = throw IOException("Upstream connection interrupted")
                },
                HttpStatus.OK,
            ).apply {
                headers.contentType = MediaType.APPLICATION_PDF
                headers.contentLength = 1024
                headers.set(HttpHeaders.CONTENT_ENCODING, "gzip")
            }
        }

        mockMvc.get("$fileUrl/download").andExpect {
            status { isBadGateway() }
            header { doesNotExist(HttpHeaders.CONTENT_DISPOSITION) }
            header { doesNotExist(HttpHeaders.CONTENT_LENGTH) }
            header { doesNotExist(HttpHeaders.CONTENT_TYPE) }
            header { doesNotExist(HttpHeaders.CONTENT_ENCODING) }
        }
    }

    @Test
    fun `returns not found for an unknown file`() {
        mockMvc
            .get("$bodyUrl/file/00000000-0000-0000-0000-000000000000")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for a file requested through an unknown body`() {
        mockMvc
            .get(
                "https://example.org:8443/oparl/v1.1/body/" +
                    "00000000-0000-0000-0000-000000000000/file/" +
                    "3d663f77-05ca-4a14-b788-68ad340ea271",
            ).andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for a file belonging to another body`() {
        mockMvc
            .get(
                "https://example.org:8443/oparl/v1.1/body/" +
                    "ffffffff-ffff-4fff-afff-ffffffffffff/file/" +
                    "3d663f77-05ca-4a14-b788-68ad340ea271",
            ).andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found when listing files for an unknown body`() {
        mockMvc
            .get(
                "https://example.org:8443/oparl/v1.1/body/" +
                    "00000000-0000-0000-0000-000000000000/file",
            ).andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found when accessing an unknown file`() {
        mockMvc
            .get(
                "$bodyUrl/file/00000000-0000-0000-0000-000000000000/access",
            ).andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for binary endpoints through another body or unknown file`() {
        for (endpoint in listOf("access", "download")) {
            mockMvc
                .get(
                    "https://example.org:8443/oparl/v1.1/body/ffffffff-ffff-4fff-afff-ffffffffffff/" +
                        "file/3d663f77-05ca-4a14-b788-68ad340ea271/$endpoint",
                ).andExpect { status { isNotFound() } }
            mockMvc
                .get("$bodyUrl/file/00000000-0000-0000-0000-000000000000/$endpoint")
                .andExpect { status { isNotFound() } }
        }
    }

    private fun updateFile(assignments: String) {
        jdbcTemplate.update(
            "UPDATE oparl_resolved_file SET $assignments WHERE id = '3d663f77-05ca-4a14-b788-68ad340ea271'",
        )
    }

    @TestConfiguration(proxyBeanMethods = false)
    class UpstreamConfiguration {
        @Bean
        @Primary
        fun controlledUpstream() = ControlledUpstream()
    }

    class ControlledUpstream : FileContentFetcher {
        private val builder = RestClient.builder()
        val server: MockRestServiceServer = MockRestServiceServer.bindTo(builder).build()
        private val client = OparlFileContentClient(builder)

        override fun fetch(
            url: OparlUrl,
            consume: (FileContent) -> Unit,
        ) = client.fetch(url, consume)
    }
}
