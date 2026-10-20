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
class OparlLegislativeTermControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    @Test
    fun `lists resolved legislative terms belonging to a body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/legislativeterm"
        )
            .andExpect {
                status { isOk() }
                jsonPath("$.id") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/legislativeterm?page=1"
                    )
                }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/List") }
                jsonPath("$.data.length()") { value(1) }
                jsonPath("$.data[0].id") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/legislativeterm/" +
                            "20ae1f2f-991a-4ea1-a546-27869c8c542a"
                    )
                }
                jsonPath("$.data[0].type") {
                    value("https://schema.oparl.org/1.1/LegislativeTerm")
                }
                jsonPath("$.data[0].body") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3"
                    )
                }
                jsonPath("$.data[0].name") { value("21. Wahlperiode") }
                jsonPath("$.data[0].startDate") { value("2025-11-01") }
                jsonPath("$.data[0].endDate") { value("2030-10-31") }
                jsonPath("$.data[0].license") { value("CC0-1.0") }
                jsonPath("$.data[0].keyword.length()") { value(2) }
                jsonPath("$.data[0].web") {
                    value("https://neuoffenstadt.example.com/rat/wahlperioden/21")
                }
                jsonPath("$.pagination.currentPage") { value(1) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(1) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/legislativeterm?page=1"
                    )
                }
                jsonPath("$.links.self") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/legislativeterm?page=1"
                    )
                }
                jsonPath("$.links.last") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/legislativeterm?page=1"
                    )
                }
                jsonPath("$.links.prev") { doesNotExist() }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `paginates legislative terms and returns empty page when out of range`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/legislativeterm?page=2"
        )
            .andExpect {
                status { isOk() }
                jsonPath("$.id") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/legislativeterm?page=2"
                    )
                }
                jsonPath("$.data.length()") { value(0) }
                jsonPath("$.pagination.currentPage") { value(2) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(1) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/legislativeterm?page=1"
                    )
                }
                jsonPath("$.links.self") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/legislativeterm?page=2"
                    )
                }
                jsonPath("$.links.prev") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/legislativeterm?page=1"
                    )
                }
                jsonPath("$.links.last") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/legislativeterm?page=1"
                    )
                }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `returns a single resolved legislative term`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/legislativeterm/" +
                "20ae1f2f-991a-4ea1-a546-27869c8c542a"
        )
            .andExpect {
                status { isOk() }
                jsonPath("$.id") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/legislativeterm/" +
                            "20ae1f2f-991a-4ea1-a546-27869c8c542a"
                    )
                }
                jsonPath("$.type") {
                    value("https://schema.oparl.org/1.1/LegislativeTerm")
                }
                jsonPath("$.body") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3"
                    )
                }
                jsonPath("$.name") { value("21. Wahlperiode") }
            }
    }

    @Test
    fun `returns not found for an unknown legislative term`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/legislativeterm/" +
                "00000000-0000-0000-0000-000000000000"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for a legislative term requested through an unknown body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "00000000-0000-0000-0000-000000000000/legislativeterm/" +
                "20ae1f2f-991a-4ea1-a546-27869c8c542a"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for a legislative term belonging to another body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "ffffffff-ffff-4fff-afff-ffffffffffff/legislativeterm/" +
                "20ae1f2f-991a-4ea1-a546-27869c8c542a"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found when listing legislative terms for an unknown body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "00000000-0000-0000-0000-000000000000/legislativeterm"
        )
            .andExpect {
                status { isNotFound() }
            }
    }
}
