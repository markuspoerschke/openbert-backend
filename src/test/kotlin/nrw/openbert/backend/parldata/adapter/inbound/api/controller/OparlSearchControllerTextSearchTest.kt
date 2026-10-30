package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import nrw.openbert.backend.TestcontainersConfiguration
import org.hamcrest.Matchers
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
class OparlSearchControllerTextSearchTest(
    @Autowired private val mockMvc: MockMvc,
) {
    private val searchUrl = "https://example.org:8443/oparl/v1.1/search"
    private val secondBodyUrl =
        "https://example.org:8443/oparl/v1.1/body/ffffffff-ffff-4fff-afff-ffffffffffff"

    @Test
    fun `searches resolved file text case insensitively`() {
        mockMvc
            .get(searchUrl) {
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
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.currentPage") { value(1) }
                jsonPath("$.pagination.totalPages") { value(1) }
            }
    }

    @Test
    fun `searches text fields of every exposed resolved OParl entity type`() {
        val searchesByExpectedType =
            mapOf(
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
            mockMvc
                .get(searchUrl) {
                    param("text", text)
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.data.length()") { value(Matchers.greaterThan(0)) }
                    jsonPath("$.data[0].type") {
                        value("https://schema.oparl.org/1.1/$expectedType")
                    }
                }
        }
    }

    @Test
    fun `excludes source systems without individual local endpoints`() {
        listOf("demo search system", "https://schema.oparl.org/1.1/").forEach { text ->
            mockMvc
                .get(searchUrl) {
                    param("text", text)
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.data.length()") { value(0) }
                    jsonPath("$.pagination.totalElements") { value(0) }
                }
        }
    }

    @Test
    fun `matches literal substrings including pattern metacharacters`() {
        listOf("ARCHABL", "budget%2026", "budget_2026", "budget\\2026", "budget!2026").forEach { text ->
            mockMvc
                .get(searchUrl) {
                    param("text", text)
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.pagination.totalElements") { value(if (text == "ARCHABL") 2 else 1) }
                }
        }

        listOf("searchable%", "searchable_", "budget2026").forEach { text ->
            mockMvc
                .get(searchUrl) {
                    param("text", text)
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.data.length()") { value(0) }
                    jsonPath("$.pagination.totalElements") { value(0) }
                }
        }
    }

    @Test
    fun `excludes deleted matches from results counts and pagination`() {
        mockMvc
            .get(searchUrl) {
                param("text", "searchable")
                param("page", "3")
                param("size", "1")
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.length()") { value(0) }
                jsonPath("$.pagination.totalElements") { value(2) }
                jsonPath("$.pagination.totalPages") { value(2) }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `paginates one result set across entity types`() {
        mockMvc
            .get(searchUrl) {
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
                jsonPath("$.links.next") { value("$searchUrl?page=2&size=2&text=haushalt") }
            }
    }

    @Test
    fun `paginates search results and provides navigation links`() {
        mockMvc
            .get(searchUrl) {
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
                jsonPath("$.links.first") { value("$searchUrl?page=1&size=1&text=searchable") }
                jsonPath("$.links.prev") { doesNotExist() }
                jsonPath("$.links.self") { value("$searchUrl?page=1&size=1&text=searchable") }
                jsonPath("$.links.next") { value("$searchUrl?page=2&size=1&text=searchable") }
                jsonPath("$.links.last") { value("$searchUrl?page=2&size=1&text=searchable") }
            }

        mockMvc
            .get(searchUrl) {
                param("text", "searchable")
                param("page", "2")
                param("size", "1")
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.length()") { value(1) }
                jsonPath("$.data[0].id") {
                    value("$secondBodyUrl/file/33333333-3333-4333-a333-333333333333")
                }
                jsonPath("$.links.prev") { value("$searchUrl?page=1&size=1&text=searchable") }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `returns an empty page when text does not match`() {
        mockMvc
            .get(searchUrl) {
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
        listOf(" ", "a", "ab", "a b c", "%%%", "ab_cd").forEach { text ->
            mockMvc
                .get(searchUrl) {
                    param("text", text)
                }.andExpect {
                    status { isBadRequest() }
                }
        }

        mockMvc
            .get(searchUrl) {
                param("text", "searchable")
                param("page", "0")
            }.andExpect {
                status { isBadRequest() }
            }

        mockMvc
            .get(searchUrl) {
                param("text", "searchable")
                param("size", "101")
            }.andExpect {
                status { isBadRequest() }
            }
    }
}
