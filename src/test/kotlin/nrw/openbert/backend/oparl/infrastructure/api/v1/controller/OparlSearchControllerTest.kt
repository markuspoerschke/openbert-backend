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
class OparlSearchControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    private val searchUrl = "https://example.org:8443/oparl/v1.1/search"
    private val secondBodyUrl =
        "https://example.org:8443/oparl/v1.1/body/ffffffff-ffff-4fff-afff-ffffffffffff"

    @Test
    fun `searches resolved file text case insensitively`() {
        mockMvc.get(searchUrl) {
            param("text", "PUBLIC TRANSPORT")
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(searchUrl) }
            jsonPath("$.type") { value("https://schema.oparl.org/1.1/List") }
            jsonPath("$.data.length()") { value(1) }
            jsonPath("$.data[0].id") {
                value("$secondBodyUrl/file/22222222-2222-4222-a222-222222222222")
            }
            jsonPath("$.data[0].text") {
                value("A searchable document about public transport.")
            }
            jsonPath("$.pagination.totalElements") { value(1) }
            jsonPath("$.pagination.elementsPerPage") { value(20) }
            jsonPath("$.pagination.currentPage") { value(1) }
            jsonPath("$.pagination.totalPages") { value(1) }
        }
    }

    @Test
    fun `searches text fields of every resolved OParl entity type`() {
        val searchesByExpectedType = mapOf(
            "demo search system" to "System",
            "Stadtparlament" to "Body",
            "21. Wahlperiode" to "LegislativeTerm",
            "Bürgerfraktion" to "Organization",
            "Mustermann" to "Person",
            "mitgliedschaften/29-34" to "Membership",
            "durchgeführt" to "Meeting",
            "Geändert beschlossen" to "AgendaItem",
            "V-2026-42" to "Paper",
            "Beschlussfassung" to "Consultation",
            "Placeholder for a council document" to "File",
            "Rathausplatz 1" to "Location",
        )

        searchesByExpectedType.forEach { (text, expectedType) ->
            mockMvc.get(searchUrl) {
                param("text", text)
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.length()") { value(org.hamcrest.Matchers.greaterThan(0)) }
                jsonPath("$.data[0].type") {
                    value("https://schema.oparl.org/1.1/$expectedType")
                }
            }
        }
    }

    @Test
    fun `paginates one result set across entity types`() {
        mockMvc.get(searchUrl) {
            param("text", "haushalt")
            param("page", "1")
            param("size", "2")
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.length()") { value(2) }
            jsonPath("$.data[0].type") {
                value("https://schema.oparl.org/1.1/Organization")
            }
            jsonPath("$.data[1].type") {
                value("https://schema.oparl.org/1.1/Meeting")
            }
            jsonPath("$.pagination.totalElements") { value(5) }
            jsonPath("$.pagination.currentPage") { value(1) }
            jsonPath("$.pagination.totalPages") { value(3) }
            jsonPath("$.links.next") { value("$searchUrl?text=haushalt&page=2&size=2") }
        }
    }

    @Test
    fun `paginates search results and provides navigation links`() {
        mockMvc.get(searchUrl) {
            param("text", "searchable")
            param("page", "1")
            param("size", "1")
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.length()") { value(1) }
            jsonPath("$.data[0].id") {
                value("$secondBodyUrl/file/22222222-2222-4222-a222-222222222222")
            }
            jsonPath("$.pagination.totalElements") { value(2) }
            jsonPath("$.pagination.elementsPerPage") { value(1) }
            jsonPath("$.pagination.currentPage") { value(1) }
            jsonPath("$.pagination.totalPages") { value(2) }
            jsonPath("$.links.first") { value("$searchUrl?text=searchable&page=1&size=1") }
            jsonPath("$.links.prev") { doesNotExist() }
            jsonPath("$.links.self") { value("$searchUrl?text=searchable&page=1&size=1") }
            jsonPath("$.links.next") { value("$searchUrl?text=searchable&page=2&size=1") }
            jsonPath("$.links.last") { value("$searchUrl?text=searchable&page=2&size=1") }
        }

        mockMvc.get(searchUrl) {
            param("text", "searchable")
            param("page", "2")
            param("size", "1")
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.length()") { value(1) }
            jsonPath("$.data[0].id") {
                value("$secondBodyUrl/file/33333333-3333-4333-a333-333333333333")
            }
            jsonPath("$.links.prev") { value("$searchUrl?text=searchable&page=1&size=1") }
            jsonPath("$.links.next") { doesNotExist() }
        }
    }

    @Test
    fun `returns an empty page when text does not match`() {
        mockMvc.get(searchUrl) {
            param("text", "not-present")
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.length()") { value(0) }
            jsonPath("$.pagination.totalElements") { value(0) }
            jsonPath("$.pagination.totalPages") { value(0) }
            jsonPath("$.links.next") { doesNotExist() }
        }
    }

    @Test
    fun `rejects invalid search parameters`() {
        mockMvc.get(searchUrl) {
            param("text", " ")
        }.andExpect {
            status { isBadRequest() }
        }

        mockMvc.get(searchUrl) {
            param("text", "searchable")
            param("page", "0")
        }.andExpect {
            status { isBadRequest() }
        }

        mockMvc.get(searchUrl) {
            param("text", "searchable")
            param("size", "101")
        }.andExpect {
            status { isBadRequest() }
        }
    }
}
