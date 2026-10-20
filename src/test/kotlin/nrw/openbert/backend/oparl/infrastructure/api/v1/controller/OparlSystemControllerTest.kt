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
class OparlSystemControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    @Test
    fun `returns system links based on request host`() {
        mockMvc.get("https://example.org:8443/oparl/v1.1/system")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("https://example.org:8443/oparl/v1.1/system") }
                jsonPath("$.body") { value("https://example.org:8443/oparl/v1.1/body?page=1") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/System") }
            }
    }
}