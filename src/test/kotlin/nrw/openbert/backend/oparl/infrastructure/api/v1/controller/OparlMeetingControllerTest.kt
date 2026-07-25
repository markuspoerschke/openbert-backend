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
class OparlMeetingControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    private val bodyUrl =
        "https://example.org:8443/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3"
    private val meetingUrl = "$bodyUrl/meeting/1dd8b01e-a39c-4b40-9f24-46f7a42a3795"

    @Test
    fun `lists resolved meetings belonging to a body`() {
        mockMvc.get("$bodyUrl/meeting")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$bodyUrl/meeting") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/List") }
                jsonPath("$.data.length()") { value(1) }
                jsonPath("$.data[0].id") { value(meetingUrl) }
                jsonPath("$.data[0].type") {
                    value("https://schema.oparl.org/1.1/Meeting")
                }
                jsonPath("$.data[0].name") {
                    value("4. Sitzung des Finanzausschusses")
                }
                jsonPath("$.data[0].meetingState") { value("durchgeführt") }
                jsonPath("$.data[0].cancelled") { value(false) }
                jsonPath("$.data[0].start") { isString() }
                jsonPath("$.data[0].end") { isString() }
                jsonPath("$.data[0].license") { value("CC0-1.0") }
                jsonPath("$.data[0].keyword.length()") { value(2) }
                jsonPath("$.data[0].web") {
                    value("https://neuoffenstadt.example.com/sitzungen/finanzausschuss-4")
                }
            }
    }

    @Test
    fun `returns a single resolved meeting`() {
        mockMvc.get(meetingUrl)
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(meetingUrl) }
                jsonPath("$.type") {
                    value("https://schema.oparl.org/1.1/Meeting")
                }
                jsonPath("$.name") { value("4. Sitzung des Finanzausschusses") }
                jsonPath("$.meetingState") { value("durchgeführt") }
            }
    }

    @Test
    fun `returns not found for an unknown meeting`() {
        mockMvc.get("$bodyUrl/meeting/00000000-0000-0000-0000-000000000000")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for a meeting requested through an unknown body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "00000000-0000-0000-0000-000000000000/meeting/" +
                "1dd8b01e-a39c-4b40-9f24-46f7a42a3795"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for a meeting belonging to another body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "ffffffff-ffff-4fff-afff-ffffffffffff/meeting/" +
                "1dd8b01e-a39c-4b40-9f24-46f7a42a3795"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found when listing meetings for an unknown body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "00000000-0000-0000-0000-000000000000/meeting"
        )
            .andExpect {
                status { isNotFound() }
            }
    }
}
