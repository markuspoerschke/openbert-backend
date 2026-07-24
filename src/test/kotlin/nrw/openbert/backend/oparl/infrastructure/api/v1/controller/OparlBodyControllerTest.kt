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
class OparlBodyControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    @Test
    fun `lists only resolved bodies`() {
        mockMvc.get("https://example.org:8443/oparl/v1.1/body")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("https://example.org:8443/oparl/v1.1/body") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/List") }
                jsonPath("$.data.length()") { value(2) }
                jsonPath("$.data[0].id") {
                    value("https://example.org:8443/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3")
                }
                jsonPath("$.data[0].type") { value("https://schema.oparl.org/1.1/Body") }
                jsonPath("$.data[0].name") { value("Neu Offenstadt – Stadtparlament") }
                jsonPath("$.data[0].organization") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/organization"
                    )
                }
                jsonPath("$.data[0].person") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/person"
                    )
                }
                jsonPath("$.data[0].meeting") { value("TODO") }
                jsonPath("$.data[0].paper") { value("TODO") }
                jsonPath("$.data[0].legislativeTerm.length()") { value(1) }
                jsonPath("$.data[0].legislativeTerm[0].id") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/legislativeterm/" +
                            "20ae1f2f-991a-4ea1-a546-27869c8c542a"
                    )
                }
                jsonPath("$.data[0].legislativeTerm[0].type") {
                    value("https://schema.oparl.org/1.1/LegislativeTerm")
                }
                jsonPath("$.data[0].legislativeTerm[0].name") { value("21. Wahlperiode") }
                jsonPath("$.data[0].legislativeTerm[0].startDate") { value("2025-11-01") }
                jsonPath("$.data[0].legislativeTerm[0].endDate") { value("2030-10-31") }
                jsonPath("$.data[0].agendaItem") { value("TODO") }
                jsonPath("$.data[0].consultation") { value("TODO") }
                jsonPath("$.data[0].file") { value("TODO") }
                jsonPath("$.data[0].locationList") { value("TODO") }
                jsonPath("$.data[0].legislativeTermList") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/legislativeterm"
                    )
                }
                jsonPath("$.data[0].membership") { value("TODO") }
            }
    }
}
