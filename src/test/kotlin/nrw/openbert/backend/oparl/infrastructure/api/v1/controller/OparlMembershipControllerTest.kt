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
class OparlMembershipControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    private val bodyUrl = "https://example.org:8443/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3"

    @Test
    fun `lists resolved memberships belonging to a body`() {
        mockMvc.get("$bodyUrl/membership")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$bodyUrl/membership?page=1") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/List") }
                jsonPath("$.data.length()") { value(1) }
                jsonPath("$.data[0].id") { value("$bodyUrl/membership/47fdc541-cf5b-4e45-9bd4-46c8dcca9f34") }
                jsonPath("$.data[0].type") { value("https://schema.oparl.org/1.1/Membership") }
                jsonPath("$.data[0].person") { value("$bodyUrl/person/5e4cb965-6c76-4b31-a6ce-e8b26e68fb8b") }
                jsonPath("$.data[0].organization") {
                    value(
                        "$bodyUrl/organization/9f7d37f9-cd93-4b11-a868-19a6c1e39318"
                    )
                }
                jsonPath("$.data[0].role") { value("Vorsitz") }
                jsonPath("$.data[0].votingRight") { value(true) }
                jsonPath("$.data[0].startDate") { value("2025-11-01") }
                jsonPath("$.data[0].endDate") { value("2030-10-31") }
                jsonPath("$.data[0].onBehalfOf") { value("$bodyUrl/organization/b6b1f18d-3147-4a2b-af0e-34f6204dc1d9") }
                jsonPath("$.data[0].license") { value("CC0-1.0") }
                jsonPath("$.data[0].keyword.length()") { value(2) }
                jsonPath("$.data[0].web") { value("https://neuoffenstadt.example.com/mitgliedschaften/29-34") }
                jsonPath("$.pagination.currentPage") { value(1) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(1) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") { value("$bodyUrl/membership?page=1") }
                jsonPath("$.links.self") { value("$bodyUrl/membership?page=1") }
                jsonPath("$.links.last") { value("$bodyUrl/membership?page=1") }
                jsonPath("$.links.prev") { doesNotExist() }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `paginates memberships and returns empty page when out of range`() {
        mockMvc.get("$bodyUrl/membership?page=2")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$bodyUrl/membership?page=2") }
                jsonPath("$.data.length()") { value(0) }
                jsonPath("$.pagination.currentPage") { value(2) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(1) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") { value("$bodyUrl/membership?page=1") }
                jsonPath("$.links.self") { value("$bodyUrl/membership?page=2") }
                jsonPath("$.links.prev") { value("$bodyUrl/membership?page=1") }
                jsonPath("$.links.last") { value("$bodyUrl/membership?page=1") }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `returns bad request when page number is less than 1`() {
        mockMvc.get("$bodyUrl/membership?page=0")
            .andExpect {
                status { isBadRequest() }
            }

        mockMvc.get("$bodyUrl/membership?page=-1")
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `returns a single resolved membership`() {
        mockMvc.get("$bodyUrl/membership/47fdc541-cf5b-4e45-9bd4-46c8dcca9f34")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$bodyUrl/membership/47fdc541-cf5b-4e45-9bd4-46c8dcca9f34") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/Membership") }
                jsonPath("$.person") { value("$bodyUrl/person/5e4cb965-6c76-4b31-a6ce-e8b26e68fb8b") }
                jsonPath("$.organization") { value("$bodyUrl/organization/9f7d37f9-cd93-4b11-a868-19a6c1e39318") }
                jsonPath("$.onBehalfOf") { value("$bodyUrl/organization/b6b1f18d-3147-4a2b-af0e-34f6204dc1d9") }
            }
    }

    @Test
    fun `returns not found for an unknown membership`() {
        mockMvc.get("$bodyUrl/membership/00000000-0000-0000-0000-000000000000")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for a membership requested through an unknown body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "00000000-0000-0000-0000-000000000000/membership/" +
                "47fdc541-cf5b-4e45-9bd4-46c8dcca9f34"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for a membership belonging to another body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "ffffffff-ffff-4fff-afff-ffffffffffff/membership/" +
                "47fdc541-cf5b-4e45-9bd4-46c8dcca9f34"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found when listing memberships for an unknown body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "00000000-0000-0000-0000-000000000000/membership"
        )
            .andExpect {
                status { isNotFound() }
            }
    }
}
