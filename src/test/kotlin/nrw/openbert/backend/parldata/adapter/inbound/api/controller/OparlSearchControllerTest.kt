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
class OparlSearchControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    private val searchUrl = "/oparl/v1.1/search"
    private val firstBodyId = "903c26aa-dc8f-4d34-afd6-afdc5040dbd3"
    private val secondBodyId = "ffffffff-ffff-4fff-afff-ffffffffffff"

    @Test
    fun `searches with body filter`() {
        // Search in first body only
        mockMvc
            .get(searchUrl) {
                param("text", "haushalt")
                param("bodies", firstBodyId)
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.length()") { value(5) }
            }

        // Search in second body only
        mockMvc
            .get(searchUrl) {
                param("text", "mobility")
                param("bodies", secondBodyId)
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.length()") { value(1) }
            }
    }

    @Test
    fun `searches with type filter`() {
        // Search for organizations only
        mockMvc
            .get(searchUrl) {
                param("text", "haushalt")
                param("types", "ORGANIZATION")
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.length()") { value(1) }
                jsonPath("$.data[0].type") { value("https://schema.oparl.org/1.1/Organization") }
            }
    }

    @Test
    fun `searches with combined filters`() {
        // Search for files in second body only
        mockMvc
            .get(searchUrl) {
                param("text", "searchable")
                param("bodies", secondBodyId)
                param("types", "FILE")
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.length()") { value(2) }
                jsonPath("$.data[0].type") { value("https://schema.oparl.org/1.1/File") }
            }
    }

    @Test
    fun `includes new parameters in pagination links`() {
        mockMvc
            .get(searchUrl) {
                param("text", "searchable")
                param("bodies", secondBodyId)
                param("types", "FILE")
                param("page", "1")
                param("size", "1")
            }.andExpect {
                status { isOk() }
                jsonPath("$.links.first") { exists() }
                jsonPath("$.links.next") { exists() }
            }
    }

    @Test
    fun `handles invalid type values gracefully`() {
        mockMvc
            .get(searchUrl) {
                param("text", "searchable")
                param("types", "INVALID_TYPE")
            }.andExpect {
                status { isOk() }
                jsonPath("$.data.length()") { value(2) } // Should ignore invalid type and return all types
            }
    }

    @Test
    fun `allows search without text parameter`() {
        mockMvc
            .get(searchUrl) {
                param("bodies", firstBodyId)
                param("types", "ORGANIZATION")
            }.andExpect {
                status { isOk() }
                // Should return all organizations in the first body
                jsonPath("$.data.length()") { value(2) }
                jsonPath("$.data[0].type") { value("https://schema.oparl.org/1.1/Organization") }
            }
    }
}
