package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import nrw.openbert.backend.TestcontainersConfiguration
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.options

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@AutoConfigureMockMvc
class OparlSystemControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    @Test
    fun `returns system links based on request host`() {
        mockMvc
            .get("https://example.org:8443/oparl/v1.1/system")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("https://example.org:8443/oparl/v1.1/system") }
                jsonPath("$.body") { value("https://example.org:8443/oparl/v1.1/body?page=1") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/System") }
            }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "https://app.openbert.de",
            "http://preview.app.openbert.de:8080",
            "http://localhost:5173",
            "https://localhost",
        ],
    )
    fun `allows browser requests from subdomains and localhost`(origin: String) {
        mockMvc
            .get("https://api.openbert.de/oparl/v1.1/system") {
                header(HttpHeaders.ORIGIN, origin)
            }.andExpect {
                status { isOk() }
                header { string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin) }
            }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "https://openbert.de",
            "https://notopenbert.de",
            "https://app.openbert.de.example.org",
            "http://localhost.example.org:5173",
        ],
    )
    fun `rejects origins outside the allowed hosts`(origin: String) {
        mockMvc
            .get("https://api.openbert.de/oparl/v1.1/system") {
                header(HttpHeaders.ORIGIN, origin)
            }.andExpect {
                status { isForbidden() }
                header { doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN) }
            }
    }

    @ParameterizedTest
    @ValueSource(strings = ["https://app.openbert.de", "http://localhost:5173"])
    fun `allows preflight requests from subdomains and localhost`(origin: String) {
        mockMvc
            .options("https://api.openbert.de/oparl/v1.1/system") {
                header(HttpHeaders.ORIGIN, origin)
                header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type")
            }.andExpect {
                status { isOk() }
                header {
                    string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin)
                    string(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "content-type")
                }
            }
    }
}
