package nrw.openbert.backend.oparl.infrastructure.api.v1.controller

import nrw.openbert.backend.TestcontainersConfiguration
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@AutoConfigureMockMvc
class OparlFileControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    private val bodyUrl =
        "https://example.org:8443/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3"
    private val fileUrl = "$bodyUrl/file/3d663f77-05ca-4a14-b788-68ad340ea271"

    @Test
    fun `lists resolved files belonging to a body`() {
        mockMvc.get("$bodyUrl/file")
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
                            "a675ad7a8ce9dcd7a2b61631fda9d8605bb4853a3509886ce348517f103f4f099"
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
        mockMvc.get("$bodyUrl/file?page=2")
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
    fun `returns a single resolved file`() {
        mockMvc.get(fileUrl)
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(fileUrl) }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/File") }
                jsonPath("$.accessUrl") { value("$fileUrl/access") }
                jsonPath("$.downloadUrl") { value("$fileUrl/download") }
            }
    }

    @Test
    fun `serves placeholder file content without forcing a download`() {
        mockMvc.get("$fileUrl/access")
            .andExpect {
                status { isOk() }
                content { contentType("text/plain") }
                content {
                    string(
                        "Placeholder for mock-council-document.txt.\n" +
                            "The original file content is not available.\n"
                    )
                }
                header { doesNotExist(HttpHeaders.CONTENT_DISPOSITION) }
                header { string("X-OpenBERT-Mock-File", "true") }
            }
    }

    @Test
    fun `serves placeholder file content as an attachment from download url`() {
        mockMvc.get("$fileUrl/download")
            .andExpect {
                status { isOk() }
                content { contentType("text/plain") }
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
                header { string("X-OpenBERT-Mock-File", "true") }
            }
    }

    @Test
    fun `returns not found for an unknown file`() {
        mockMvc.get("$bodyUrl/file/00000000-0000-0000-0000-000000000000")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for a file requested through an unknown body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "00000000-0000-0000-0000-000000000000/file/" +
                "3d663f77-05ca-4a14-b788-68ad340ea271"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for a file belonging to another body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "ffffffff-ffff-4fff-afff-ffffffffffff/file/" +
                "3d663f77-05ca-4a14-b788-68ad340ea271"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found when listing files for an unknown body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "00000000-0000-0000-0000-000000000000/file"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found when accessing an unknown file`() {
        mockMvc.get(
            "$bodyUrl/file/00000000-0000-0000-0000-000000000000/access"
        )
            .andExpect {
                status { isNotFound() }
            }
    }
}
