package nrw.openbert.backend.oparl.infrastructure.api.v1.controller

import nrw.openbert.backend.TestcontainersConfiguration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@AutoConfigureMockMvc
class OparlFileControllerTest(@Autowired private val mockMvc: MockMvc) {
    private val bodyId = "903c26aa-dc8f-4d34-afd6-afdc5040dbd3"
    private val fileId = "d40a0262-d7cf-4a9f-9374-e6b98b54a020"

    @Test
    fun `lists resolved files for a body`() {
        mockMvc.get("https://example.org:8443/oparl/v1.1/body/$bodyId/file")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("https://example.org:8443/oparl/v1.1/body/$bodyId/file") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/List") }
                jsonPath("$.data.length()") { value(1) }
                jsonPath("$.data[0].id") { value("https://example.org:8443/oparl/v1.1/file/$fileId") }
                jsonPath("$.data[0].name") { value("Haushaltsplan 2026") }
            }
    }

    @Test
    fun `gets a resolved file`() {
        mockMvc.get("https://example.org:8443/oparl/v1.1/file/$fileId")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("https://example.org:8443/oparl/v1.1/file/$fileId") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/File") }
                jsonPath("$.fileName") { value("haushaltsplan-2026.pdf") }
                jsonPath("$.date") { value("2026-01-15") }
                jsonPath("$.accessUrl") { value("https://neuoffenstadt.example.com/files/haushaltsplan-2026.pdf") }
                jsonPath("$.keyword[0]") { value("haushalt") }
            }
    }

    @Test
    fun `returns not found for an unknown file or body`() {
        mockMvc.get("/oparl/v1.1/file/00000000-0000-0000-0000-000000000000")
            .andExpect { status { isNotFound() } }
        mockMvc.get("/oparl/v1.1/body/00000000-0000-0000-0000-000000000000/file")
            .andExpect { status { isNotFound() } }
    }
}
