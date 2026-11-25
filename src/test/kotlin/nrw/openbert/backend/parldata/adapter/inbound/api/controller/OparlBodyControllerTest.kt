package nrw.openbert.backend.parldata.adapter.inbound.api.controller

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
        mockMvc
            .get("https://example.org:8443/oparl/v1.1/body?sort=id,asc")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("https://example.org:8443/oparl/v1.1/body?page=1&sort=id,asc") }
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
                        "$bodyUrl/legislativeterm/20ae1f2f-991a-4ea1-a546-27869c8c542a",
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
                jsonPath("$.links.first") { value("https://example.org:8443/oparl/v1.1/body?page=1&sort=id,asc") }
                jsonPath("$.links.self") { value("https://example.org:8443/oparl/v1.1/body?page=1&sort=id,asc") }
                jsonPath("$.links.last") { value("https://example.org:8443/oparl/v1.1/body?page=1&sort=id,asc") }
                jsonPath("$.links.prev") { doesNotExist() }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `sorts by id descending by default and allows overriding the sort order`() {
        mockMvc
            .get("https://example.org:8443/oparl/v1.1/body")
            .andExpect {
                status { isOk() }
                jsonPath("$.data.length()") { value(2) }
                jsonPath("$.data[0].id") {
                    value("https://example.org:8443/oparl/v1.1/body/ffffffff-ffff-4fff-afff-ffffffffffff")
                }
                jsonPath("$.data[1].id") { value(bodyUrl) }
                jsonPath("$.links.self") { value("https://example.org:8443/oparl/v1.1/body?page=1") }
            }

        mockMvc
            .get("https://example.org:8443/oparl/v1.1/body?sort=created,desc")
            .andExpect {
                status { isOk() }
                jsonPath("$.data.length()") { value(2) }
                jsonPath("$.links.self") {
                    value("https://example.org:8443/oparl/v1.1/body?page=1&sort=created,desc")
                }
            }

        mockMvc
            .get("https://example.org:8443/oparl/v1.1/body?sort=name")
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `paginates bodies and returns empty page when out of range`() {
        mockMvc
            .get("https://example.org:8443/oparl/v1.1/body?page=2")
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
        mockMvc
            .get("https://example.org:8443/oparl/v1.1/body?page=0")
            .andExpect {
                status { isBadRequest() }
            }

        mockMvc
            .get("https://example.org:8443/oparl/v1.1/body?page=-1")
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `filters the body list by created_until and preserves the filter in the links`() {
        mockMvc
            .get("https://example.org:8443/oparl/v1.1/body?created_until=2026-07-22T00:00:00Z")
            .andExpect {
                status { isOk() }
                jsonPath("$.data.length()") { value(0) }
                jsonPath("$.pagination.totalElements") { value(0) }
                jsonPath("$.links.self") {
                    value("https://example.org:8443/oparl/v1.1/body?page=1&created_until=2026-07-22T00:00:00Z")
                }
            }

        mockMvc
            .get("https://example.org:8443/oparl/v1.1/body?created_until=2026-07-23T00:00:00Z")
            .andExpect {
                status { isOk() }
                jsonPath("$.data.length()") { value(2) }
            }
    }

    @Test
    fun `counts only non-deleted resources belonging to the requested body`() {
        mockMvc.get("$bodyUrl/statistics").andExpect {
            status { isOk() }
            jsonPath("$.persons") { value(1) }
            jsonPath("$.organizations") { value(2) }
            jsonPath("$.paper") { value(1) }
            jsonPath("$.files") { value(1) }
            jsonPath("$.meetings") { value(1) }
            jsonPath("$.agendaItems") { value(2) }
            jsonPath("$.locations") { value(1) }
            jsonPath("$.consultations") { value(1) }
            jsonPath("$.legislativeTerms") { value(1) }
            jsonPath("$.memberships") { value(1) }
        }

        mockMvc.get("/oparl/v1.1/body/ffffffff-ffff-4fff-afff-ffffffffffff/statistics").andExpect {
            status { isOk() }
            jsonPath("$.persons") { value(0) }
            jsonPath("$.organizations") { value(0) }
            jsonPath("$.paper") { value(0) }
            jsonPath("$.files") { value(2) }
            jsonPath("$.meetings") { value(0) }
            jsonPath("$.agendaItems") { value(0) }
            jsonPath("$.locations") { value(0) }
            jsonPath("$.consultations") { value(0) }
            jsonPath("$.legislativeTerms") { value(0) }
            jsonPath("$.memberships") { value(0) }
        }
    }

    @Test
    fun `returns not found for statistics of an unknown body`() {
        mockMvc.get("/oparl/v1.1/body/00000000-0000-4000-8000-000000000000/statistics").andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `documents body statistics in OpenAPI`() {
        val responses = "$.paths['/oparl/v1.1/body/{bodyId}/statistics'].get.responses"
        mockMvc.get("/docs/openapi.json").andExpect {
            status { isOk() }
            jsonPath("$responses['200'].content['application/json'].schema['\$ref']") {
                value("#/components/schemas/OparlBodyStatisticsDto")
            }
            jsonPath("$responses['404'].content['application/problem+json']") { exists() }
        }
    }
}
