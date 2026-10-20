package nrw.openbert.backend.parldata.adapter.inbound.mcp

import io.modelcontextprotocol.client.McpClient
import io.modelcontextprotocol.client.McpSyncClient
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest
import io.modelcontextprotocol.spec.McpSchema.TextContent
import nrw.openbert.backend.TestcontainersConfiguration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.web.client.RestClient
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ObjectMapper

@Import(TestcontainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ResourceToolTest(
    @Autowired private val objectMapper: ObjectMapper,
) {
    @LocalServerPort
    private var port: Int = 0

    private val baseUrl get() = "http://localhost:$port"

    @Test
    fun `advertises a single required id parameter`() = withClient { client ->
        val tool = client.listTools().tools().single { it.name() == "oparl_get_resource" }
        val schema = objectMapper.valueToTree<JsonNode>(tool.inputSchema())
        assertEquals(objectMapper.readTree("[\"id\"]"), schema["required"])
        assertEquals(setOf("id"), schema["properties"].propertyNames().toSet())
        assertTrue(tool.annotations().readOnlyHint())
    }

    @Test
    fun `retrieves every searchable entity using only its returned id`() = withClient { client ->
        val searchesByType = mapOf(
            "Body" to "Stadtparlament",
            "LegislativeTerm" to "21. Wahlperiode",
            "Organization" to "Bürgerfraktion",
            "Person" to "Mustermann",
            "Membership" to "mitgliedschaften/29-34",
            "Meeting" to "durchgeführt",
            "AgendaItem" to "Geändert beschlossen",
            "Paper" to "V-2026-42",
            "Consultation" to "Beschlussfassung",
            "File" to "public transport",
            "Location" to "Rathausplatz 1",
        )
        searchesByType.forEach { (type, term) ->
            val results = call(client, "oparl_search", mapOf("term" to term))
            assertFalse(results.isEmpty, "No search results for $term")
            results.forEach { searchResult ->
                assertEquals("https://schema.oparl.org/1.1/$type", searchResult["type"].asString())
                val id = searchResult["id"].asString()
                val resource = call(client, "oparl_get_resource", mapOf("id" to id))
                assertEquals(searchResult, resource)
                val apiJson = RestClient.create().get().uri(id).retrieve().body(String::class.java)
                assertEquals(objectMapper.readTree(apiJson), resource)
            }
        }
    }

    @Test
    fun `follows body list and relationship ids from search results`() = withClient { client ->
        val body = call(client, "oparl_body_list").first()
        assertEquals(body, call(client, "oparl_get_resource", mapOf("id" to body["id"].asString())))

        val agendaItem = call(client, "oparl_search", mapOf("term" to "Geändert beschlossen")).first()
        val meeting = call(client, "oparl_get_resource", mapOf("id" to agendaItem["meeting"].asString()))
        assertEquals("https://schema.oparl.org/1.1/Meeting", meeting["type"].asString())
        val file = agendaItem["resolutionFile"]
        assertEquals(file, call(client, "oparl_get_resource", mapOf("id" to file["id"].asString())))
    }

    @Test
    fun `reports malformed unsupported missing raw-only and incorrectly scoped ids as tool errors`() = withClient { client ->
        val file = call(client, "oparl_search", mapOf("term" to "public transport")).first()
        val fileId = file["id"].asString()
        val bodyPath = "$baseUrl/oparl/v1.1/body/903c26aa-dc8f-4d34-afd6-afdc5040dbd3"
        val invalidIds = listOf(
            "",
            "22222222-2222-4222-a222-222222222222",
            fileId.removePrefix(baseUrl),
            fileId.replace(baseUrl, "https://upstream.example.org"),
            "$bodyPath/file/not-a-uuid",
            "$bodyPath/file/1-1-1-1-1",
            "$bodyPath/unsupported/22222222-2222-4222-a222-222222222222",
            "$bodyPath/file/00000000-0000-4000-8000-000000000000",
            "$bodyPath/file/aaaaaaaa-aaaa-4aaa-aaaa-aaaaaaaaaaaa",
            fileId.replace("ffffffff-ffff-4fff-afff-ffffffffffff", "903c26aa-dc8f-4d34-afd6-afdc5040dbd3"),
            "$baseUrl/oparl/v1.1/body/00000000-0000-4000-8000-000000000000",
            "$bodyPath/file",
            "$fileId/download",
            "$fileId?unexpected=true",
            "$fileId#fragment",
            "$baseUrl/oparl/v1.1/system",
        )
        invalidIds.forEach { id ->
            val result = client.callTool(CallToolRequest.builder("oparl_get_resource").arguments(mapOf("id" to id)).build())
            assertTrue(result.isError() == true, "Expected a tool error for $id: $result")
        }
    }

    private fun call(client: McpSyncClient, name: String, arguments: Map<String, Any> = emptyMap()): JsonNode {
        val result = client.callTool(CallToolRequest.builder(name).arguments(arguments).build())
        assertFalse(result.isError() == true, "$name failed: $result")
        return objectMapper.readTree(result.content().filterIsInstance<TextContent>().single().text())
    }

    private fun <T> withClient(action: (McpSyncClient) -> T): T {
        val transport = HttpClientStreamableHttpTransport.builder(baseUrl).build()
        return McpClient.sync(transport).build().use { client ->
            client.initialize()
            action(client)
        }
    }
}
