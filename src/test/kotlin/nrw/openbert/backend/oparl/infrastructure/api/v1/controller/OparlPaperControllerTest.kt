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
class OparlPaperControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    private val bodyUrl =
        "https://example.org:8443/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3"
    private val paperUrl = "$bodyUrl/paper/f6d47f4c-e5db-4dab-91f8-3bafc825d30c"

    @Test
    fun `lists resolved papers belonging to a body`() {
        mockMvc.get("$bodyUrl/paper")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$bodyUrl/paper") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/List") }
                jsonPath("$.data.length()") { value(1) }
                jsonPath("$.data[0].id") { value(paperUrl) }
                jsonPath("$.data[0].type") {
                    value("https://schema.oparl.org/1.1/Paper")
                }
                jsonPath("$.data[0].body") { value(bodyUrl) }
                jsonPath("$.data[0].name") {
                    value("Haushaltssatzung für das Jahr 2027")
                }
                jsonPath("$.data[0].reference") { value("V-2026-42") }
                jsonPath("$.data[0].date") { value("2026-07-21") }
                jsonPath("$.data[0].paperType") { value("Beschlussvorlage") }
                jsonPath("$.data[0].license") { value("CC0-1.0") }
                jsonPath("$.data[0].keyword.length()") { value(2) }
                jsonPath("$.data[0].web") {
                    value("https://neuoffenstadt.example.com/drucksachen/V-2026-42")
                }
            }
    }

    @Test
    fun `returns a single resolved paper`() {
        mockMvc.get(paperUrl)
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(paperUrl) }
                jsonPath("$.type") {
                    value("https://schema.oparl.org/1.1/Paper")
                }
                jsonPath("$.body") { value(bodyUrl) }
                jsonPath("$.reference") { value("V-2026-42") }
            }
    }

    @Test
    fun `returns not found for an unknown paper`() {
        mockMvc.get("$bodyUrl/paper/00000000-0000-0000-0000-000000000000")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for a paper requested through an unknown body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "00000000-0000-0000-0000-000000000000/paper/" +
                "f6d47f4c-e5db-4dab-91f8-3bafc825d30c"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for a paper belonging to another body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "ffffffff-ffff-4fff-afff-ffffffffffff/paper/" +
                "f6d47f4c-e5db-4dab-91f8-3bafc825d30c"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found when listing papers for an unknown body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "00000000-0000-0000-0000-000000000000/paper"
        )
            .andExpect {
                status { isNotFound() }
            }
    }
}
