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
class OparlConsultationControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    private val bodyUrl =
        "https://example.org:8443/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3"
    private val consultationUrl =
        "$bodyUrl/consultation/c92d284d-6899-49c2-9422-70ad7f4f38ea"
    private val paperUrl = "$bodyUrl/paper/f6d47f4c-e5db-4dab-91f8-3bafc825d30c"
    private val agendaItemUrl = "$bodyUrl/agendaitem/7b728a96-ae9a-40d1-b010-cc909ab2dd87"
    private val meetingUrl = "$bodyUrl/meeting/1dd8b01e-a39c-4b40-9f24-46f7a42a3795"
    private val organizationUrl =
        "$bodyUrl/organization/9f7d37f9-cd93-4b11-a868-19a6c1e39318"

    @Test
    fun `lists resolved consultations belonging to a body`() {
        mockMvc
            .get("$bodyUrl/consultation")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$bodyUrl/consultation?page=1") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/List") }
                jsonPath("$.data.length()") { value(1) }
                jsonPath("$.data[0].id") { value(consultationUrl) }
                jsonPath("$.data[0].type") {
                    value("https://schema.oparl.org/1.1/Consultation")
                }
                jsonPath("$.data[0].paper") { value(paperUrl) }
                jsonPath("$.data[0].agendaItem") { value(agendaItemUrl) }
                jsonPath("$.data[0].meeting") { value(meetingUrl) }
                jsonPath("$.data[0].organization.length()") { value(1) }
                jsonPath("$.data[0].organization[0]") { value(organizationUrl) }
                jsonPath("$.data[0].authoritative") { value(true) }
                jsonPath("$.data[0].role") { value("Beschlussfassung") }
                jsonPath("$.data[0].license") { value("CC0-1.0") }
                jsonPath("$.data[0].keyword.length()") { value(2) }
                jsonPath("$.data[0].web") {
                    value("https://neuoffenstadt.example.com/beratungen/47594")
                }
                jsonPath("$.pagination.currentPage") { value(1) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(1) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") { value("$bodyUrl/consultation?page=1") }
                jsonPath("$.links.self") { value("$bodyUrl/consultation?page=1") }
                jsonPath("$.links.last") { value("$bodyUrl/consultation?page=1") }
                jsonPath("$.links.prev") { doesNotExist() }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `paginates consultations and returns empty page when out of range`() {
        mockMvc
            .get("$bodyUrl/consultation?page=2")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$bodyUrl/consultation?page=2") }
                jsonPath("$.data.length()") { value(0) }
                jsonPath("$.pagination.currentPage") { value(2) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(1) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") { value("$bodyUrl/consultation?page=1") }
                jsonPath("$.links.self") { value("$bodyUrl/consultation?page=2") }
                jsonPath("$.links.prev") { value("$bodyUrl/consultation?page=1") }
                jsonPath("$.links.last") { value("$bodyUrl/consultation?page=1") }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `returns bad request when page number is less than 1`() {
        mockMvc
            .get("$bodyUrl/consultation?page=0")
            .andExpect {
                status { isBadRequest() }
            }

        mockMvc
            .get("$bodyUrl/consultation?page=-1")
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `returns a single resolved consultation`() {
        mockMvc
            .get(consultationUrl)
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(consultationUrl) }
                jsonPath("$.type") {
                    value("https://schema.oparl.org/1.1/Consultation")
                }
                jsonPath("$.paper") { value(paperUrl) }
                jsonPath("$.agendaItem") { value(agendaItemUrl) }
                jsonPath("$.meeting") { value(meetingUrl) }
            }
    }

    @Test
    fun `returns not found for an unknown consultation`() {
        mockMvc
            .get("$bodyUrl/consultation/00000000-0000-0000-0000-000000000000")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for a consultation requested through an unknown body`() {
        mockMvc
            .get(
                "https://example.org:8443/oparl/v1.1/body/" +
                    "00000000-0000-0000-0000-000000000000/consultation/" +
                    "c92d284d-6899-49c2-9422-70ad7f4f38ea",
            ).andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for a consultation belonging to another body`() {
        mockMvc
            .get(
                "https://example.org:8443/oparl/v1.1/body/" +
                    "ffffffff-ffff-4fff-afff-ffffffffffff/consultation/" +
                    "c92d284d-6899-49c2-9422-70ad7f4f38ea",
            ).andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found when listing consultations for an unknown body`() {
        mockMvc
            .get(
                "https://example.org:8443/oparl/v1.1/body/" +
                    "00000000-0000-0000-0000-000000000000/consultation",
            ).andExpect {
                status { isNotFound() }
            }
    }
}
