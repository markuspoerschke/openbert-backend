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
class OparlMeetingControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    private val bodyUrl =
        "https://example.org:8443/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3"
    private val meetingUrl = "$bodyUrl/meeting/1dd8b01e-a39c-4b40-9f24-46f7a42a3795"
    private val firstAgendaItemUrl =
        "$bodyUrl/agendaitem/7b728a96-ae9a-40d1-b010-cc909ab2dd87"
    private val secondAgendaItemUrl =
        "$bodyUrl/agendaitem/11111111-1111-4111-a111-111111111111"
    private val organizationUrl =
        "$bodyUrl/organization/9f7d37f9-cd93-4b11-a868-19a6c1e39318"

    @Test
    fun `lists resolved meetings belonging to a body`() {
        mockMvc
            .get("$bodyUrl/meeting")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$bodyUrl/meeting?page=1") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/List") }
                jsonPath("$.data.length()") { value(1) }
                jsonPath("$.data[0].id") { value(meetingUrl) }
                jsonPath("$.data[0].type") {
                    value("https://schema.oparl.org/1.1/Meeting")
                }
                jsonPath("$.data[0].name") {
                    value("4. Sitzung des Finanzausschusses")
                }
                jsonPath("$.data[0].meetingState") { value("durchgeführt") }
                jsonPath("$.data[0].cancelled") { value(false) }
                jsonPath("$.data[0].start") { isString() }
                jsonPath("$.data[0].end") { isString() }
                jsonPath("$.data[0].organization.length()") { value(1) }
                jsonPath("$.data[0].organization[0]") { value(organizationUrl) }
                jsonPath("$.data[0].agendaItem.length()") { value(2) }
                jsonPath("$.data[0].agendaItem[0].id") { value(firstAgendaItemUrl) }
                jsonPath("$.data[0].agendaItem[0].order") { value(0) }
                jsonPath("$.data[0].agendaItem[1].id") { value(secondAgendaItemUrl) }
                jsonPath("$.data[0].agendaItem[1].order") { value(1) }
                jsonPath("$.data[0].license") { value("CC0-1.0") }
                jsonPath("$.data[0].keyword.length()") { value(2) }
                jsonPath("$.data[0].web") {
                    value("https://neuoffenstadt.example.com/sitzungen/finanzausschuss-4")
                }
                jsonPath("$.pagination.currentPage") { value(1) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(1) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") { value("$bodyUrl/meeting?page=1") }
                jsonPath("$.links.self") { value("$bodyUrl/meeting?page=1") }
                jsonPath("$.links.last") { value("$bodyUrl/meeting?page=1") }
                jsonPath("$.links.prev") { doesNotExist() }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `paginates meetings and returns empty page when out of range`() {
        mockMvc
            .get("$bodyUrl/meeting?page=2")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$bodyUrl/meeting?page=2") }
                jsonPath("$.data.length()") { value(0) }
                jsonPath("$.pagination.currentPage") { value(2) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(1) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") { value("$bodyUrl/meeting?page=1") }
                jsonPath("$.links.self") { value("$bodyUrl/meeting?page=2") }
                jsonPath("$.links.prev") { value("$bodyUrl/meeting?page=1") }
                jsonPath("$.links.last") { value("$bodyUrl/meeting?page=1") }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `returns bad request when page number is less than 1`() {
        mockMvc
            .get("$bodyUrl/meeting?page=0")
            .andExpect {
                status { isBadRequest() }
            }

        mockMvc
            .get("$bodyUrl/meeting?page=-1")
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `returns a single resolved meeting`() {
        mockMvc
            .get(meetingUrl)
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(meetingUrl) }
                jsonPath("$.type") {
                    value("https://schema.oparl.org/1.1/Meeting")
                }
                jsonPath("$.name") { value("4. Sitzung des Finanzausschusses") }
                jsonPath("$.meetingState") { value("durchgeführt") }
                jsonPath("$.organization.length()") { value(1) }
                jsonPath("$.organization[0]") { value(organizationUrl) }
                jsonPath("$.agendaItem.length()") { value(2) }
                jsonPath("$.agendaItem[0].id") { value(firstAgendaItemUrl) }
                jsonPath("$.agendaItem[1].id") { value(secondAgendaItemUrl) }
            }
    }

    @Test
    fun `returns not found for an unknown meeting`() {
        mockMvc
            .get("$bodyUrl/meeting/00000000-0000-0000-0000-000000000000")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for a meeting requested through an unknown body`() {
        mockMvc
            .get(
                "https://example.org:8443/oparl/v1.1/body/" +
                    "00000000-0000-0000-0000-000000000000/meeting/" +
                    "1dd8b01e-a39c-4b40-9f24-46f7a42a3795",
            ).andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for a meeting belonging to another body`() {
        mockMvc
            .get(
                "https://example.org:8443/oparl/v1.1/body/" +
                    "ffffffff-ffff-4fff-afff-ffffffffffff/meeting/" +
                    "1dd8b01e-a39c-4b40-9f24-46f7a42a3795",
            ).andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found when listing meetings for an unknown body`() {
        mockMvc
            .get(
                "https://example.org:8443/oparl/v1.1/body/" +
                    "00000000-0000-0000-0000-000000000000/meeting",
            ).andExpect {
                status { isNotFound() }
            }
    }
}
