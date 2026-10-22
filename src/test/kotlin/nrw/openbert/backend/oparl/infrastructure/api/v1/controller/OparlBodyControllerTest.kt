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
    private val bodyUrl = "https://example.org:8443/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3"

    @Test
    fun `lists only resolved bodies`() {
        mockMvc.get("https://example.org:8443/oparl/v1.1/body")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("https://example.org:8443/oparl/v1.1/body?page=1") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/List") }
                jsonPath("$.data.length()") { value(2) }
                jsonPath("$.data[0].id") { value(bodyUrl) }
                jsonPath("$.data[0].type") { value("https://schema.oparl.org/1.1/Body") }
                jsonPath("$.data[0].name") { value("Neu Offenstadt – Stadtparlament") }
                jsonPath("$.data[0].organization") { value("$bodyUrl/organization?page=1") }
                jsonPath("$.data[0].person") { value("$bodyUrl/person?page=1") }
                jsonPath("$.data[0].meeting") { value("$bodyUrl/meeting?page=1") }
                jsonPath("$.data[0].paper") { value("$bodyUrl/paper?page=1") }
                jsonPath("$.data[0].legislativeTerm.length()") { value(1) }
                jsonPath("$.data[0].legislativeTerm[0].id") {
                    value(
                        "$bodyUrl/legislativeterm/20ae1f2f-991a-4ea1-a546-27869c8c542a"
                    )
                }
                jsonPath("$.data[0].legislativeTerm[0].type") { value("https://schema.oparl.org/1.1/LegislativeTerm") }
                jsonPath("$.data[0].legislativeTerm[0].name") { value("21. Wahlperiode") }
                jsonPath("$.data[0].legislativeTerm[0].startDate") { value("2025-11-01") }
                jsonPath("$.data[0].legislativeTerm[0].endDate") { value("2030-10-31") }
                jsonPath("$.data[0].agendaItem") { value("$bodyUrl/agendaitem?page=1") }
                jsonPath("$.data[0].consultation") { value("$bodyUrl/consultation?page=1") }
                jsonPath("$.data[0].file") { value("$bodyUrl/file?page=1") }
                jsonPath("$.data[0].locationList") { value("$bodyUrl/location?page=1") }
                jsonPath("$.data[0].legislativeTermList") { value("$bodyUrl/legislativeterm?page=1") }
                jsonPath("$.data[0].membership") { value("$bodyUrl/membership?page=1") }
                jsonPath("$.pagination.currentPage") { value(1) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(2) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") { value("https://example.org:8443/oparl/v1.1/body?page=1") }
                jsonPath("$.links.self") { value("https://example.org:8443/oparl/v1.1/body?page=1") }
                jsonPath("$.links.last") { value("https://example.org:8443/oparl/v1.1/body?page=1") }
                jsonPath("$.links.prev") { doesNotExist() }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `paginates bodies and returns empty page when out of range`() {
        mockMvc.get("https://example.org:8443/oparl/v1.1/body?page=2")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("https://example.org:8443/oparl/v1.1/body?page=2") }
                jsonPath("$.data.length()") { value(0) }
                jsonPath("$.pagination.currentPage") { value(2) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(2) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") { value("https://example.org:8443/oparl/v1.1/body?page=1") }
                jsonPath("$.links.self") { value("https://example.org:8443/oparl/v1.1/body?page=2") }
                jsonPath("$.links.prev") { value("https://example.org:8443/oparl/v1.1/body?page=1") }
                jsonPath("$.links.last") { value("https://example.org:8443/oparl/v1.1/body?page=1") }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `returns bad request when page number is less than 1`() {
        mockMvc.get("https://example.org:8443/oparl/v1.1/body?page=0")
            .andExpect {
                status { isBadRequest() }
            }

        mockMvc.get("https://example.org:8443/oparl/v1.1/body?page=-1")
            .andExpect {
                status { isBadRequest() }
            }
    }
}
