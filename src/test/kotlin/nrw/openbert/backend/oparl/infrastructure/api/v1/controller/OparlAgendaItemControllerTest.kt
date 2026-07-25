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
class OparlAgendaItemControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    private val bodyUrl =
        "https://example.org:8443/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3"
    private val agendaItemUrl = "$bodyUrl/agendaitem/7b728a96-ae9a-40d1-b010-cc909ab2dd87"
    private val meetingUrl = "$bodyUrl/meeting/1dd8b01e-a39c-4b40-9f24-46f7a42a3795"
    private val fileUrl = "$bodyUrl/file/3d663f77-05ca-4a14-b788-68ad340ea271"

    @Test
    fun `lists resolved agenda items belonging to a body`() {
        mockMvc.get("$bodyUrl/agendaitem")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value("$bodyUrl/agendaitem") }
                jsonPath("$.type") { value("https://schema.oparl.org/1.1/List") }
                jsonPath("$.data.length()") { value(1) }
                jsonPath("$.data[0].id") { value(agendaItemUrl) }
                jsonPath("$.data[0].type") {
                    value("https://schema.oparl.org/1.1/AgendaItem")
                }
                jsonPath("$.data[0].meeting") { value(meetingUrl) }
                jsonPath("$.data[0].number") { value("10.1") }
                jsonPath("$.data[0].order") { value(0) }
                jsonPath("$.data[0].name") {
                    value("Haushaltssatzung für das Jahr 2027")
                }
                jsonPath("$.data[0].public") { value(true) }
                jsonPath("$.data[0].result") { value("Geändert beschlossen") }
                jsonPath("$.data[0].resolutionText") {
                    value("Der Haushaltsansatz wurde entsprechend der Beratung angepasst.")
                }
                jsonPath("$.data[0].resolutionFile.id") { value(fileUrl) }
                jsonPath("$.data[0].auxiliaryFile.length()") { value(1) }
                jsonPath("$.data[0].auxiliaryFile[0].id") { value(fileUrl) }
                jsonPath("$.data[0].start") { isString() }
                jsonPath("$.data[0].end") { isString() }
                jsonPath("$.data[0].license") { value("CC0-1.0") }
                jsonPath("$.data[0].keyword.length()") { value(2) }
                jsonPath("$.data[0].web") {
                    value(
                        "https://neuoffenstadt.example.com/" +
                            "sitzungen/finanzausschuss-4/top-10-1"
                    )
                }
            }
    }

    @Test
    fun `returns a single resolved agenda item with its mandatory order`() {
        mockMvc.get(agendaItemUrl)
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(agendaItemUrl) }
                jsonPath("$.type") {
                    value("https://schema.oparl.org/1.1/AgendaItem")
                }
                jsonPath("$.meeting") { value(meetingUrl) }
                jsonPath("$.name") { value("Haushaltssatzung für das Jahr 2027") }
                jsonPath("$.order") { value(0) }
            }
    }

    @Test
    fun `returns not found for an unknown agenda item`() {
        mockMvc.get("$bodyUrl/agendaitem/00000000-0000-0000-0000-000000000000")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for an agenda item requested through an unknown body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "00000000-0000-0000-0000-000000000000/agendaitem/" +
                "7b728a96-ae9a-40d1-b010-cc909ab2dd87"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found for an agenda item belonging to another body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "ffffffff-ffff-4fff-afff-ffffffffffff/agendaitem/" +
                "7b728a96-ae9a-40d1-b010-cc909ab2dd87"
        )
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `returns not found when listing agenda items for an unknown body`() {
        mockMvc.get(
            "https://example.org:8443/oparl/v1.1/body/" +
                "00000000-0000-0000-0000-000000000000/agendaitem"
        )
            .andExpect {
                status { isNotFound() }
            }
    }
}
