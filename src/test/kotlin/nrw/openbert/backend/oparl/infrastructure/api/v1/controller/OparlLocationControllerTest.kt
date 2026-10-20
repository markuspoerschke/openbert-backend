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
class OparlLocationControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    @Test
    fun `lists resolved locations belonging to a body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/location"
        )
            .andExpect {
                status { isOk() }
                jsonPath("$.id") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/location?page=1"
                    )
                }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/List") }
                jsonPath("$.data.length()") { value(1) }
                jsonPath("$.data[0].id") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/location/" +
                            "a70c1459-c98f-4e9c-a02f-4bb21b837c04"
                    )
                }
                jsonPath("$.data[0].type") {
                    value("https://schema.oparl.org/1.1/Location")
                }
                jsonPath("$.data[0].description") {
                    value("Rathaus Neu Offenstadt, Rathausplatz 1, 12345 Neu Offenstadt")
                }
                jsonPath("$.data[0].geojson.type") { value("Feature") }
                jsonPath("$.data[0].geojson.geometry.type") { value("Point") }
                jsonPath("$.data[0].streetAddress") { value("Rathausplatz 1") }
                jsonPath("$.data[0].room") { value("Ratssaal") }
                jsonPath("$.data[0].postalCode") { value("12345") }
                jsonPath("$.data[0].subLocality") { value("Innenstadt") }
                jsonPath("$.data[0].locality") { value("Neu Offenstadt") }
                jsonPath("$.data[0].bodies.length()") { value(1) }
                jsonPath("$.data[0].bodies[0]") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3"
                    )
                }
                jsonPath("$.data[0].license") { value("CC0-1.0") }
                jsonPath("$.data[0].keyword.length()") { value(2) }
                jsonPath("$.data[0].web") {
                    value("https://neuoffenstadt.example.com/rathaus")
                }
                jsonPath("$.pagination.currentPage") { value(1) }
                jsonPath("$.pagination.elementsPerPage") { value(25) }
                jsonPath("$.pagination.totalElements") { value(1) }
                jsonPath("$.pagination.totalPages") { value(1) }
                jsonPath("$.links.first") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/location?page=1"
                    )
                }
                jsonPath("$.links.self") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/location?page=1"
                    )
                }
                jsonPath("$.links.last") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/location?page=1"
                    )
                }
                jsonPath("$.links.prev") { doesNotExist() }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `paginates locations and returns empty page when out of range`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/location?page=2"
        )
            .andExpect {
                status { isOk() }
                jsonPath("$.id") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/location?page=2"
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
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/location?page=1"
                    )
                }
                jsonPath("$.links.self") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/location?page=2"
                    )
                }
                jsonPath("$.links.prev") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/location?page=1"
                    )
                }
                jsonPath("$.links.last") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/location?page=1"
                    )
                }
                jsonPath("$.links.next") { doesNotExist() }
            }
    }

    @Test
    fun `returns a single resolved location`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/location/" +
                "a70c1459-c98f-4e9c-a02f-4bb21b837c04"
        )
            .andExpect {
                status { isOk() }
                jsonPath("$.id") {
                    value(
                        "https://example.org:8443/oparl/v1.1/body/" +
                            "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/location/" +
                            "a70c1459-c98f-4e9c-a02f-4bb21b837c04"
                    )
                }
                jsonPath("$.type") {
                    value("https://schema.oparl.org/1.1/Location")
                }
                jsonPath("$.description") {
                    value("Rathaus Neu Offenstadt, Rathausplatz 1, 12345 Neu Offenstadt")
                }
            }
    }

    @Test
    fun `returns not found for an unknown location`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "903c26aa-dc8f-4d34-afd6-afdc5040dbd3/location/" +
                "00000000-0000-0000-0000-000000000000"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for a location requested through an unknown body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "00000000-0000-0000-0000-000000000000/location/" +
                "a70c1459-c98f-4e9c-a02f-4bb21b837c04"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for a location belonging to another body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "ffffffff-ffff-4fff-afff-ffffffffffff/location/" +
                "a70c1459-c98f-4e9c-a02f-4bb21b837c04"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found when listing locations for an unknown body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "00000000-0000-0000-0000-000000000000/location"
        )
            .andExpect {
                status { isNotFound() }
            }
    }
}
