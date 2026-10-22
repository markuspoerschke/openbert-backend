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
class OparlOrganizationControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    private val bodyUrl = "https://example.org:8443/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3"

    @Test
    fun `lists resolved organizations belonging to a body`() {
        mockMvc.get("$bodyUrl/organization")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$bodyUrl/organization?page=1") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/List") }
                jsonPath("$.data.length()") { value(2) }
                jsonPath("$.data[0].id") { value("$bodyUrl/organization/9f7d37f9-cd93-4b11-a868-19a6c1e39318") }
                jsonPath("$.data[0].type") { value("https://schema.oparl.org/1.1/Organization") }
                jsonPath("$.data[0].body") { value(bodyUrl) }
                jsonPath("$.data[0].name") { value("Ausschuss für Haushalt und Finanzen") }
                jsonPath("$.data[0].shortName") { value("Finanzausschuss") }
                jsonPath("$.data[0].post.length()") { value(2) }
                jsonPath("$.data[0].organizationType") { value("Gremium") }
                jsonPath("$.data[0].classification") { value("Ausschuss") }
                jsonPath("$.data[0].startDate") { value("2012-07-17") }
                jsonPath("$.data[0].website") { value("https://finanzen.neuoffenstadt.example.com") }
                jsonPath("$.data[0].keyword.length()") { value(2) }
                jsonPath("$.data[0].membership.length()") { value(1) }
                jsonPath("$.data[0].membership[0]") {
                    value(
                        "$bodyUrl/membership/47fdc541-cf5b-4e45-9bd4-46c8dcca9f34"
                    )
                }
                jsonPath("$.data[0].meeting") {
                    value(
                        "$bodyUrl/organization/9f7d37f9-cd93-4b11-a868-19a6c1e39318/meeting?page=1"
                    )
                }
                jsonPath("$.data[0].consultation") {
                    value(
                        "$bodyUrl/organization/9f7d37f9-cd93-4b11-a868-19a6c1e39318/consultation?page=1"
                    )
                }
                jsonPath("$.pagination.currentPage") { value(1) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(2) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") { value("$bodyUrl/organization?page=1") }
                jsonPath("$.links.self") { value("$bodyUrl/organization?page=1") }
                jsonPath("$.links.last") { value("$bodyUrl/organization?page=1") }
                jsonPath("$.links.prev") { doesNotExist() }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `paginates organizations and returns empty page when out of range`() {
        mockMvc.get("$bodyUrl/organization?page=2")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$bodyUrl/organization?page=2") }
                jsonPath("$.data.length()") { value(0) }
                jsonPath("$.pagination.currentPage") { value(2) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(2) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") { value("$bodyUrl/organization?page=1") }
                jsonPath("$.links.self") { value("$bodyUrl/organization?page=2") }
                jsonPath("$.links.prev") { value("$bodyUrl/organization?page=1") }
                jsonPath("$.links.last") { value("$bodyUrl/organization?page=1") }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `returns bad request when page number is less than 1`() {
        mockMvc.get("$bodyUrl/organization?page=0")
            .andExpect {
                status { isBadRequest() }
            }

        mockMvc.get("$bodyUrl/organization?page=-1")
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `returns a single resolved organization`() {
        mockMvc.get("$bodyUrl/organization/9f7d37f9-cd93-4b11-a868-19a6c1e39318")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$bodyUrl/organization/9f7d37f9-cd93-4b11-a868-19a6c1e39318") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/Organization") }
                jsonPath("$.body") { value(bodyUrl) }
                jsonPath("$.name") { value("Ausschuss für Haushalt und Finanzen") }
                jsonPath("$.membership.length()") { value(1) }
                jsonPath("$.meeting") {
                    value(
                        "$bodyUrl/organization/9f7d37f9-cd93-4b11-a868-19a6c1e39318/meeting?page=1"
                    )
                }
                jsonPath("$.consultation") {
                    value(
                        "$bodyUrl/organization/9f7d37f9-cd93-4b11-a868-19a6c1e39318/consultation?page=1"
                    )
                }
            }
    }

    @Test
    fun `returns not found for unknown organization`() {
        mockMvc.get("$bodyUrl/organization/00000000-0000-0000-0000-000000000000")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for an organization requested through an unknown body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "00000000-0000-0000-0000-000000000000/organization/" +
                "9f7d37f9-cd93-4b11-a868-19a6c1e39318"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for an organization belonging to another body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "ffffffff-ffff-4fff-afff-ffffffffffff/organization/" +
                "9f7d37f9-cd93-4b11-a868-19a6c1e39318"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found when listing organizations for an unknown body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "00000000-0000-0000-0000-000000000000/organization"
        )
            .andExpect {
                status { isNotFound() }
            }
    }
}
