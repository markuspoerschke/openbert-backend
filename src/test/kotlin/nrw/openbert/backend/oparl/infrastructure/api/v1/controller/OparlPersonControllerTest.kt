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
class OparlPersonControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    private val bodyUrl = "https://example.org:8443/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3"

    @Test
    fun `lists resolved persons belonging to a body`() {
        mockMvc
            .get("$bodyUrl/person")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$bodyUrl/person?page=1") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/List") }
                jsonPath("$.data.length()") { value(1) }
                jsonPath("$.data[0].id") { value("$bodyUrl/person/5e4cb965-6c76-4b31-a6ce-e8b26e68fb8b") }
                jsonPath("$.data[0].type") { value("https://schema.oparl.org/1.1/Person") }
                jsonPath("$.data[0].body") { value(bodyUrl) }
                jsonPath("$.data[0].name") { value("Prof. Dr. Max Mustermann") }
                jsonPath("$.data[0].familyName") { value("Mustermann") }
                jsonPath("$.data[0].givenName") { value("Max") }
                jsonPath("$.data[0].title.length()") { value(2) }
                jsonPath("$.data[0].gender") { value("male") }
                jsonPath("$.data[0].phone[0]") { value("+493012345678") }
                jsonPath("$.data[0].email[0]") { value("max.mustermann@neuoffenstadt.example.com") }
                jsonPath("$.data[0].status[0]") { value("Bezirksbürgermeister") }
                jsonPath("$.data[0].keyword.length()") { value(2) }
                jsonPath("$.data[0].membership.length()") { value(1) }
                jsonPath("$.data[0].membership[0].id") {
                    value(
                        "$bodyUrl/membership/47fdc541-cf5b-4e45-9bd4-46c8dcca9f34",
                    )
                }
                jsonPath("$.data[0].membership[0].person") { doesNotExist() }
                jsonPath("$.data[0].membership[0].organization") {
                    value(
                        "$bodyUrl/organization/9f7d37f9-cd93-4b11-a868-19a6c1e39318",
                    )
                }
                jsonPath("$.pagination.currentPage") { value(1) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(1) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") { value("$bodyUrl/person?page=1") }
                jsonPath("$.links.self") { value("$bodyUrl/person?page=1") }
                jsonPath("$.links.last") { value("$bodyUrl/person?page=1") }
                jsonPath("$.links.prev") { doesNotExist() }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `paginates persons and returns empty page when out of range`() {
        mockMvc
            .get("$bodyUrl/person?page=2")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$bodyUrl/person?page=2") }
                jsonPath("$.data.length()") { value(0) }
                jsonPath("$.pagination.currentPage") { value(2) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(1) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") { value("$bodyUrl/person?page=1") }
                jsonPath("$.links.self") { value("$bodyUrl/person?page=2") }
                jsonPath("$.links.prev") { value("$bodyUrl/person?page=1") }
                jsonPath("$.links.last") { value("$bodyUrl/person?page=1") }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `returns bad request when page number is less than 1`() {
        mockMvc
            .get("$bodyUrl/person?page=0")
            .andExpect {
                status { isBadRequest() }
            }

        mockMvc
            .get("$bodyUrl/person?page=-1")
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `returns a single resolved person`() {
        mockMvc
            .get("$bodyUrl/person/5e4cb965-6c76-4b31-a6ce-e8b26e68fb8b")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$bodyUrl/person/5e4cb965-6c76-4b31-a6ce-e8b26e68fb8b") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/Person") }
                jsonPath("$.body") { value(bodyUrl) }
                jsonPath("$.name") { value("Prof. Dr. Max Mustermann") }
                jsonPath("$.membership.length()") { value(1) }
            }
    }

    @Test
    fun `returns not found for unknown person`() {
        mockMvc
            .get("$bodyUrl/person/00000000-0000-0000-0000-000000000000")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for a person requested through an unknown body`() {
        mockMvc
            .get(
                "https://example.org:8443/oparl/v1.1/body/" +
                    "00000000-0000-0000-0000-000000000000/person/" +
                    "5e4cb965-6c76-4b31-a6ce-e8b26e68fb8b",
            ).andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for a person belonging to another body`() {
        mockMvc
            .get(
                "https://example.org:8443/oparl/v1.1/body/" +
                    "ffffffff-ffff-4fff-afff-ffffffffffff/person/" +
                    "5e4cb965-6c76-4b31-a6ce-e8b26e68fb8b",
            ).andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found when listing persons for an unknown body`() {
        mockMvc
            .get(
                "https://example.org:8443/oparl/v1.1/body/" +
                    "00000000-0000-0000-0000-000000000000/person",
            ).andExpect {
                status { isNotFound() }
            }
    }
}
