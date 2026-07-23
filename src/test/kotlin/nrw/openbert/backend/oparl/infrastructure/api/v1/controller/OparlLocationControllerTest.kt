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
class OparlLocationControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    private val bodyId = "903c26aa-dc8f-4d34-afd6-afdc5040dbd3"
    private val locationId = "87e750e2-6e30-4f6a-9a74-e0a2c34a5961"

    @Test
    fun `lists resolved locations belonging to a body`() {
        mockMvc.get("https://example.org:8443/oparl/v1.1/body/$bodyId/location")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("https://example.org:8443/oparl/v1.1/body/$bodyId/location") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/List") }
                jsonPath("$.data.length()") { value(1) }
                jsonPath("$.data[0].id") { value("https://example.org:8443/oparl/v1.1/location/$locationId") }
                jsonPath("$.data[0].type") { value("https://schema.oparl.org/1.1/Location") }
                jsonPath("$.data[0].description") { value("Rathaus Neu Offenstadt") }
            }
    }

    @Test
    fun `returns a single resolved location`() {
        mockMvc.get("https://example.org:8443/oparl/v1.1/location/$locationId")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("https://example.org:8443/oparl/v1.1/location/$locationId") }
                jsonPath("$.streetAddress") { value("Rathausplatz 1") }
                jsonPath("$.room") { value("Sitzungssaal") }
                jsonPath("$.postalCode") { value("12345") }
                jsonPath("$.subLocality") { value("Mitte") }
                jsonPath("$.locality") { value("Neu Offenstadt") }
                jsonPath("$.bodies[0]") { value("https://example.org:8443/oparl/v1.1/body/$bodyId") }
                jsonPath("$.keyword.length()") { value(2) }
            }
    }

    @Test
    fun `returns not found for unknown location`() {
        mockMvc.get("https://example.org:8443/oparl/v1.1/location/00000000-0000-0000-0000-000000000000")
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun `returns not found when listing locations for an unknown body`() {
        mockMvc.get("https://example.org:8443/oparl/v1.1/body/00000000-0000-0000-0000-000000000000/location")
            .andExpect { status { isNotFound() } }
    }
}
