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
class OparlOrganizationControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    @Test
    fun `lists resolved organizations belonging to a body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/organization"
        )
            .andExpect {
                status { isOk() }
                jsonPath("$.id") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/organization"
                    )
                }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/List") }
                jsonPath("$.data.length()") { value(1) }
                jsonPath("$.data[0].id") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/organization/" +
                            "9f7d37f9-cd93-4b11-a868-19a6c1e39318"
                    )
                }
                jsonPath("$.data[0].type") { value("https://schema.oparl.org/1.1/Organization") }
                jsonPath("$.data[0].body") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3"
                    )
                }
                jsonPath("$.data[0].name") { value("Ausschuss für Haushalt und Finanzen") }
                jsonPath("$.data[0].shortName") { value("Finanzausschuss") }
                jsonPath("$.data[0].post.length()") { value(2) }
                jsonPath("$.data[0].organizationType") { value("Gremium") }
                jsonPath("$.data[0].classification") { value("Ausschuss") }
                jsonPath("$.data[0].startDate") { value("2012-07-17") }
                jsonPath("$.data[0].website") { value("https://finanzen.neuoffenstadt.example.com") }
                jsonPath("$.data[0].keyword.length()") { value(2) }
            }
    }

    @Test
    fun `returns a single resolved organization`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/organization/" +
                "9f7d37f9-cd93-4b11-a868-19a6c1e39318"
        )
            .andExpect {
                status { isOk() }
                jsonPath("$.id") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/organization/" +
                            "9f7d37f9-cd93-4b11-a868-19a6c1e39318"
                    )
                }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/Organization") }
                jsonPath("$.body") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3"
                    )
                }
                jsonPath("$.name") { value("Ausschuss für Haushalt und Finanzen") }
            }
    }

    @Test
    fun `returns not found for unknown organization`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/organization/" +
                "00000000-0000-0000-0000-000000000000"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for an organization requested through an unknown body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "00000000-0000-0000-0000-000000000000/organization/" +
                "9f7d37f9-cd93-4b11-a868-19a6c1e39318"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for an organization belonging to another body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "ffffffff-ffff-4fff-afff-ffffffffffff/organization/" +
                "9f7d37f9-cd93-4b11-a868-19a6c1e39318"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found when listing organizations for an unknown body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "00000000-0000-0000-0000-000000000000/organization"
        )
            .andExpect {
                status { isNotFound() }
            }
    }
}
