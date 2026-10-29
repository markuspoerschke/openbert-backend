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
class OparlPaperControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    private val bodyUrl =
        "https://example.org:8443/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3"
    private val paperUrl = "$bodyUrl/paper/f6d47f4c-e5db-4dab-91f8-3bafc825d30c"
    private val consultationUrl =
        "$bodyUrl/consultation/c92d284d-6899-49c2-9422-70ad7f4f38ea"

    @Test
    fun `lists resolved papers belonging to a body`() {
        mockMvc
            .get("$bodyUrl/paper")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$bodyUrl/paper?page=1") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/List") }
                jsonPath("$.data.length()") { value(1) }
                jsonPath("$.data[0].id") { value(paperUrl) }
                jsonPath("$.data[0].type") {
                    value("https://schema.oparl.org/1.1/Paper")
                }
                jsonPath("$.data[0].body") { value(bodyUrl) }
                jsonPath("$.data[0].name") {
                    value("Haushaltssatzung für das Jahr 2027")
                }
                jsonPath("$.data[0].reference") { value("V-2026-42") }
                jsonPath("$.data[0].date") { value("2026-07-21") }
                jsonPath("$.data[0].paperType") { value("Beschlussvorlage") }
                jsonPath("$.data[0].consultation.length()") { value(1) }
                jsonPath("$.data[0].consultation[0].id") { value(consultationUrl) }
                jsonPath("$.data[0].consultation[0].paper") { doesNotExist() }
                jsonPath("$.data[0].license") { value("CC0-1.0") }
                jsonPath("$.data[0].keyword.length()") { value(2) }
                jsonPath("$.data[0].web") {
                    value("https://neuoffenstadt.example.com/drucksachen/V-2026-42")
                }
                jsonPath("$.pagination.currentPage") { value(1) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(1) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") { value("$bodyUrl/paper?page=1") }
                jsonPath("$.links.self") { value("$bodyUrl/paper?page=1") }
                jsonPath("$.links.last") { value("$bodyUrl/paper?page=1") }
                jsonPath("$.links.prev") { doesNotExist() }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `paginates papers and returns empty page when out of range`() {
        mockMvc
            .get("$bodyUrl/paper?page=2")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$bodyUrl/paper?page=2") }
                jsonPath("$.data.length()") { value(0) }
                jsonPath("$.pagination.currentPage") { value(2) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(1) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") { value("$bodyUrl/paper?page=1") }
                jsonPath("$.links.self") { value("$bodyUrl/paper?page=2") }
                jsonPath("$.links.prev") { value("$bodyUrl/paper?page=1") }
                jsonPath("$.links.last") { value("$bodyUrl/paper?page=1") }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `returns bad request when page number is less than 1`() {
        mockMvc
            .get("$bodyUrl/paper?page=0")
            .andExpect {
                status { isBadRequest() }
            }

        mockMvc
            .get("$bodyUrl/paper?page=-1")
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `returns a single resolved paper`() {
        mockMvc
            .get(paperUrl)
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(paperUrl) }
                jsonPath("$.type") {
                    value("https://schema.oparl.org/1.1/Paper")
                }
                jsonPath("$.body") { value(bodyUrl) }
                jsonPath("$.reference") { value("V-2026-42") }
                jsonPath("$.consultation.length()") { value(1) }
                jsonPath("$.consultation[0].id") { value(consultationUrl) }
            }
    }

    @Test
    fun `returns not found for an unknown paper`() {
        mockMvc
            .get("$bodyUrl/paper/00000000-0000-0000-0000-000000000000")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for a paper requested through an unknown body`() {
        mockMvc
            .get(
                "https://example.org:8443/oparl/v1.1/body/" +
                    "00000000-0000-0000-0000-000000000000/paper/" +
                    "f6d47f4c-e5db-4dab-91f8-3bafc825d30c",
            ).andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for a paper belonging to another body`() {
        mockMvc
            .get(
                "https://example.org:8443/oparl/v1.1/body/" +
                    "ffffffff-ffff-4fff-afff-ffffffffffff/paper/" +
                    "f6d47f4c-e5db-4dab-91f8-3bafc825d30c",
            ).andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found when listing papers for an unknown body`() {
        mockMvc
            .get(
                "https://example.org:8443/oparl/v1.1/body/" +
                    "00000000-0000-0000-0000-000000000000/paper",
            ).andExpect {
                status { isNotFound() }
            }
    }
}
