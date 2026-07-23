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
    @Test
    fun `lists resolved meetings belonging to a body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/meeting"
        )
            .andExpect {
                status { isOk() }
                jsonPath("$.id") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/meeting"
                    )
                }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/List") }
                jsonPath("$.data.length()") { value(1) }
                jsonPath("$.data[0].id") {
                    value(
                        "https://example.org:8443/oparl/v1.1/meeting/" +
                            "1dd8b01e-a39c-4b40-9f24-46f7a42a3795"
                    )
                }
                jsonPath("$.data[0].type") { value("https://schema.oparl.org/1.1/Meeting") }
                jsonPath("$.data[0].name") { value("4. Sitzung des Finanzausschusses") }
                jsonPath("$.data[0].meetingState") { value("durchgeführt") }
                jsonPath("$.data[0].cancelled") { value(false) }
                jsonPath("$.data[0].start") { isString() }
                jsonPath("$.data[0].end") { isString() }
                jsonPath("$.data[0].license") { value("CC0-1.0") }
                jsonPath("$.data[0].keyword.length()") { value(2) }
                jsonPath("$.data[0].web") {
                    value("https://neuoffenstadt.example.com/meeting/finanzausschuss-4")
                }
            }
    }

    @Test
    fun `returns a single resolved meeting`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/meeting/" +
                "1dd8b01e-a39c-4b40-9f24-46f7a42a3795"
        )
            .andExpect {
                status { isOk() }
                jsonPath("$.id") {
                    value(
                        "https://example.org:8443/oparl/v1.1/meeting/" +
                            "1dd8b01e-a39c-4b40-9f24-46f7a42a3795"
                    )
                }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/Meeting") }
                jsonPath("$.name") { value("4. Sitzung des Finanzausschusses") }
            }
    }

    @Test
    fun `returns not found for unknown meeting`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/meeting/" +
                "00000000-0000-0000-0000-000000000000"
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
