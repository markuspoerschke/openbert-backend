package nrw.openbert.backend.crawl.infrastructure.client

import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlAgendaItemDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlBodyDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlConsultationDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlFileDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlLegislativeTermDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlListDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlLocationDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlMeetingDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlMembershipDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlOrganizationDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlPaperDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlPersonDto
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlSystemDto
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

@RestClientTest(OparlClient::class)
class OparlClientTest(
    @Autowired private val oparlClient: OparlClient,
    @Autowired private val mockServer: MockRestServiceServer,
) {
    @Test
    fun `gets a single OParl object`() {
        val url = "https://oparl.example.org/system"
        mockServer.expect(requestTo(url))
            .andExpect(method(HttpMethod.GET))
            .andRespond(
                withSuccess(
                    """
                    {
                      "id": "$url",
                      "type": "https://schema.oparl.org/1.1/System",
                      "oparlVersion": "https://schema.oparl.org/1.1/",
                      "unexpectedExtension": "is ignored"
                    }
                    """.trimIndent(),
                    MediaType.APPLICATION_JSON,
                ),
            )

        val response = assertIs<OparlSystemDto>(oparlClient.get(url))

        assertEquals(url, response.id)
        assertEquals("https://schema.oparl.org/1.1/", response.oparlVersion)
        assertNull(response.body)
        mockServer.verify()
    }

    @Test
    fun `gets a polymorphic OParl list with nullable metadata`() {
        val url = "https://oparl.example.org/objects"
        val objectTypes = listOf(
            "AgendaItem",
            "Body",
            "Consultation",
            "File",
            "LegislativeTerm",
            "Location",
            "Meeting",
            "Membership",
            "Organization",
            "Paper",
            "Person",
            "System",
        )
        val data = objectTypes.joinToString(",") { type ->
            """{"id":"https://oparl.example.org/$type","type":"https://schema.oparl.org/1.1/$type"}"""
        }
        mockServer.expect(requestTo(url))
            .andRespond(
                withSuccess(
                    """
                    {
                      "id": "$url",
                      "type": "https://schema.oparl.org/1.1/List",
                      "data": [$data],
                      "pagination": {"totalElements": 12, "serverSpecific": true},
                      "links": {"self": "$url"}
                    }
                    """.trimIndent(),
                    MediaType.APPLICATION_JSON,
                ),
            )

        val response = assertIs<OparlListDto>(oparlClient.get(url))

        assertEquals(
            listOf(
                OparlAgendaItemDto::class,
                OparlBodyDto::class,
                OparlConsultationDto::class,
                OparlFileDto::class,
                OparlLegislativeTermDto::class,
                OparlLocationDto::class,
                OparlMeetingDto::class,
                OparlMembershipDto::class,
                OparlOrganizationDto::class,
                OparlPaperDto::class,
                OparlPersonDto::class,
                OparlSystemDto::class,
            ),
            response.data?.map { it::class },
        )
        assertEquals(12, response.pagination?.totalElements)
        assertNull(response.pagination?.totalPages)
        assertEquals(url, response.links?.self)
        mockServer.verify()
    }

    @Test
    fun `recognizes an untyped OParl 1_0 list and infers types of deleted items`() {
        val url = "https://oparl.example.org/body/1/organization"
        mockServer.expect(requestTo(url))
            .andRespond(
                withSuccess(
                    """
                    {
                      "data": [
                        {
                          "id": "$url/deleted",
                          "created": "1970-01-01T00:00:00+01:00",
                          "deleted": true
                        },
                        {
                          "id": "$url/active",
                          "type": "https://schema.oparl.org/1.0/Organization",
                          "name": "Ausschuss",
                          "location": {
                            "id": "https://oparl.example.org/location/1",
                            "locality": "Bochum"
                          },
                          "serverSpecific": true
                        }
                      ],
                      "pagination": {"totalElements": 2}
                    }
                    """.trimIndent(),
                    MediaType.APPLICATION_JSON,
                ),
            )

        val response = assertIs<OparlListDto>(oparlClient.get(url))
        val deletedOrganization = assertIs<OparlOrganizationDto>(response.data?.first())
        val activeOrganization = assertIs<OparlOrganizationDto>(response.data?.last())

        assertEquals("https://schema.oparl.org/1.1/List", response.type)
        assertEquals("https://schema.oparl.org/1.0/Organization", deletedOrganization.type)
        assertEquals(true, deletedOrganization.deleted)
        assertEquals("Ausschuss", activeOrganization.name)
        assertEquals("Bochum", activeOrganization.location?.locality)
        assertEquals("https://schema.oparl.org/1.0/Location", activeOrganization.location?.type)
        mockServer.verify()
    }

    @Test
    fun `infers the item type from an all-deleted list URL`() {
        val url = "https://oparl.example.org/webservice/oparl/v1.0/body/1/organization"
        mockServer.expect(requestTo(url))
            .andRespond(
                withSuccess(
                    """
                    {
                      "data": [
                        {
                          "id": "$url/deleted",
                          "deleted": true
                        }
                      ]
                    }
                    """.trimIndent(),
                    MediaType.APPLICATION_JSON,
                ),
            )

        val response = assertIs<OparlListDto>(oparlClient.get(url))
        val organization = assertIs<OparlOrganizationDto>(response.data?.single())

        assertEquals("https://schema.oparl.org/1.0/List", response.type)
        assertEquals("https://schema.oparl.org/1.0/Organization", organization.type)
        assertEquals(true, organization.deleted)
        mockServer.verify()
    }
}
