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
class OparlOrganizationExternalListControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    private val bodyUrl =
        "https://example.org:8443/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3"
    private val organizationId = "9f7d37f9-cd93-4b11-a868-19a6c1e39318"
    private val organizationUrl = "$bodyUrl/organization/$organizationId"
    private val meetingUrl = "$bodyUrl/meeting/1dd8b01e-a39c-4b40-9f24-46f7a42a3795"
    private val consultationUrl =
        "$bodyUrl/consultation/c92d284d-6899-49c2-9422-70ad7f4f38ea"
    private val unknownId = "00000000-0000-0000-0000-000000000000"
    private val otherBodyUrl =
        "https://example.org:8443/oparl/v1.1/body/ffffffff-ffff-4fff-afff-ffffffffffff"

    @Test
    fun `lists meetings linked to an organization`() {
        mockMvc
            .get("$organizationUrl/meeting")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$organizationUrl/meeting?page=1") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/List") }
                jsonPath("$.data.length()") { value(1) }
                jsonPath("$.data[0].id") { value(meetingUrl) }
                jsonPath("$.data[0].organization.length()") { value(1) }
                jsonPath("$.data[0].organization[0]") { value(organizationUrl) }
                jsonPath("$.pagination.currentPage") { value(1) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(1) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") { value("$organizationUrl/meeting?page=1") }
                jsonPath("$.links.self") { value("$organizationUrl/meeting?page=1") }
                jsonPath("$.links.last") { value("$organizationUrl/meeting?page=1") }
                jsonPath("$.links.prev") { doesNotExist() }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `paginates organization meetings and returns empty page when out of range`() {
        mockMvc
            .get("$organizationUrl/meeting?page=2")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$organizationUrl/meeting?page=2") }
                jsonPath("$.data.length()") { value(0) }
                jsonPath("$.pagination.currentPage") { value(2) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(1) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") { value("$organizationUrl/meeting?page=1") }
                jsonPath("$.links.self") { value("$organizationUrl/meeting?page=2") }
                jsonPath("$.links.prev") { value("$organizationUrl/meeting?page=1") }
                jsonPath("$.links.last") { value("$organizationUrl/meeting?page=1") }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `returns an empty meeting list for an organization without linked meetings`() {
        val organizationWithoutMeetings =
            "$bodyUrl/organization/b6b1f18d-3147-4a2b-af0e-34f6204dc1d9"

        mockMvc
            .get("$organizationWithoutMeetings/meeting")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$organizationWithoutMeetings/meeting?page=1") }
                jsonPath("$.data.length()") { value(0) }
                jsonPath("$.pagination.currentPage") { value(1) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(0) }
                jsonPath("$.pagination.totalPages") { value(0) }
                jsonPath("$.links.first") { value("$organizationWithoutMeetings/meeting?page=1") }
                jsonPath("$.links.self") { value("$organizationWithoutMeetings/meeting?page=1") }
                jsonPath("$.links.last") { value("$organizationWithoutMeetings/meeting?page=1") }
                jsonPath("$.links.prev") { doesNotExist() }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `returns not found when listing meetings for an unknown organization`() {
        mockMvc
            .get("$bodyUrl/organization/$unknownId/meeting")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found when listing organization meetings for an unknown body`() {
        mockMvc
            .get(
                "https://example.org:8443/oparl/v1.1/body/$unknownId/" +
                    "organization/$organizationId/meeting",
            ).andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found when listing meetings through a different body`() {
        mockMvc
            .get("$otherBodyUrl/organization/$organizationId/meeting")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `lists consultations linked to an organization`() {
        mockMvc
            .get("$organizationUrl/consultation")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$organizationUrl/consultation?page=1") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/List") }
                jsonPath("$.data.length()") { value(1) }
                jsonPath("$.data[0].id") { value(consultationUrl) }
                jsonPath("$.data[0].organization.length()") { value(1) }
                jsonPath("$.data[0].organization[0]") { value(organizationUrl) }
                jsonPath("$.pagination.currentPage") { value(1) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(1) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") { value("$organizationUrl/consultation?page=1") }
                jsonPath("$.links.self") { value("$organizationUrl/consultation?page=1") }
                jsonPath("$.links.last") { value("$organizationUrl/consultation?page=1") }
                jsonPath("$.links.prev") { doesNotExist() }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `paginates organization consultations and returns empty page when out of range`() {
        mockMvc
            .get("$organizationUrl/consultation?page=2")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$organizationUrl/consultation?page=2") }
                jsonPath("$.data.length()") { value(0) }
                jsonPath("$.pagination.currentPage") { value(2) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(1) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") { value("$organizationUrl/consultation?page=1") }
                jsonPath("$.links.self") { value("$organizationUrl/consultation?page=2") }
                jsonPath("$.links.prev") { value("$organizationUrl/consultation?page=1") }
                jsonPath("$.links.last") { value("$organizationUrl/consultation?page=1") }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `returns an empty consultation list for an organization without linked consultations`() {
        val organizationWithoutConsultations =
            "$bodyUrl/organization/b6b1f18d-3147-4a2b-af0e-34f6204dc1d9"

        mockMvc
            .get("$organizationWithoutConsultations/consultation")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$organizationWithoutConsultations/consultation?page=1") }
                jsonPath("$.data.length()") { value(0) }
                jsonPath("$.pagination.currentPage") { value(1) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(0) }
                jsonPath("$.pagination.totalPages") { value(0) }
                jsonPath("$.links.first") { value("$organizationWithoutConsultations/consultation?page=1") }
                jsonPath("$.links.self") { value("$organizationWithoutConsultations/consultation?page=1") }
                jsonPath("$.links.last") { value("$organizationWithoutConsultations/consultation?page=1") }
                jsonPath("$.links.prev") { doesNotExist() }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `returns bad request when page number is less than 1 for organization meetings`() {
        mockMvc
            .get("$organizationUrl/meeting?page=0")
            .andExpect {
                status { isBadRequest() }
            }

        mockMvc
            .get("$organizationUrl/meeting?page=-1")
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `returns bad request when page number is less than 1 for organization consultations`() {
        mockMvc
            .get("$organizationUrl/consultation?page=0")
            .andExpect {
                status { isBadRequest() }
            }

        mockMvc
            .get("$organizationUrl/consultation?page=-1")
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `returns not found when listing consultations for an unknown organization`() {
        mockMvc
            .get("$bodyUrl/organization/$unknownId/consultation")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found when listing organization consultations for an unknown body`() {
        mockMvc
            .get(
                "https://example.org:8443/oparl/v1.1/body/$unknownId/" +
                    "organization/$organizationId/consultation",
            ).andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found when listing consultations through a different body`() {
        mockMvc
            .get("$otherBodyUrl/organization/$organizationId/consultation")
            .andExpect {
                status { isNotFound() }
            }
    }
}
