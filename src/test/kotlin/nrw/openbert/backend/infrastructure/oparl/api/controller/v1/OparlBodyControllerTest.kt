package nrw.openbert.backend.infrastructure.oparl.api.controller.v1

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
        mockMvc.get("https://example.org:8443/oparl/1.1/bodies")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("https://example.org:8443/oparl/1.1/bodies") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/List") }
                jsonPath("$.data.length()") { value(1) }
                jsonPath("$.data[0].id") {
                    value("https://example.org:8443/oparl/1.1/bodies/903c26aa-dc8f-4d34-afd6-afdc5040dbd3")
                }
                jsonPath("$.data[0].type") { value("https://schema.oparl.org/1.1/Body") }
                jsonPath("$.data[0].name") { value("Neu Offenstadt – Stadtparlament") }
                jsonPath("$.data[0].organization") { value("TODO") }
                jsonPath("$.data[0].person") { value("TODO") }
                jsonPath("$.data[0].meeting") { value("TODO") }
                jsonPath("$.data[0].paper") { value("TODO") }
                jsonPath("$.data[0].legislativeTerm.length()") { value(0) }
                jsonPath("$.data[0].agendaItem") { value("TODO") }
                jsonPath("$.data[0].consultation") { value("TODO") }
                jsonPath("$.data[0].file") { value("TODO") }
                jsonPath("$.data[0].locationList") { value("TODO") }
                jsonPath("$.data[0].legislativeTermList") { value("TODO") }
                jsonPath("$.data[0].membership") { value("TODO") }
            }
    }
}