package nrw.openbert.backend.crawl.infrastructure.client

import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlResponseDto
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.node.ArrayNode
import tools.jackson.databind.node.ObjectNode
import java.net.URI

@Component
class OparlClient(
    restClientBuilder: RestClient.Builder,
    private val objectMapper: ObjectMapper,
) {
    private val restClient = restClientBuilder.build()

    fun get(url: String): OparlResponseDto {
        val response = restClient
            .get()
            .uri(url)
            .retrieve()
            .requiredBody(JsonNode::class.java)

        addMissingListTypes(response, url)
        addMissingEmbeddedTypes(response)

        return objectMapper.treeToValue(response, OparlResponseDto::class.java)
    }

    private fun addMissingListTypes(response: JsonNode, url: String) {
        if (response !is ObjectNode) {
            return
        }

        val data = response.get("data")
        if (data !is ArrayNode) {
            return
        }

        if (response.textValue("type") == null) {
            response.put("type", "${inferSchema(url)}/List")
        }

        val itemTypes = data
            .mapNotNull { it.textValue("type") }
            .toSet()

        val itemType = when (itemTypes.size) {
            0 -> inferListItemType(url)
            1 -> itemTypes.single()
            else -> null
        }

        if (itemType == null) {
            return
        }

        data
            .filterIsInstance<ObjectNode>()
            .filter { it.textValue("type") == null }
            .forEach { it.put("type", itemType) }
    }

    private fun inferListItemType(url: String): String? {
        val path = runCatching { URI.create(url).path }.getOrNull() ?: return null
        val resourceName = path.trimEnd('/').substringAfterLast('/').lowercase()
        val objectType = LIST_RESOURCE_TYPES[resourceName] ?: return null
        return "${inferSchema(path)}/$objectType"
    }

    private fun inferSchema(urlOrPath: String): String {
        val path = runCatching { URI.create(urlOrPath).path }.getOrNull() ?: urlOrPath
        val schemaVersion = OPARL_VERSION_PATTERN
            .find(path)
            ?.groupValues
            ?.get(1)
            ?: "1.1"

        return "$OPARL_SCHEMA_PREFIX$schemaVersion"
    }

    private fun addMissingEmbeddedTypes(node: JsonNode, parentSchema: String? = null) {
        when (node) {
            is ObjectNode -> {
                val schema = node.textValue("type")
                    ?.substringBeforeLast('/')
                    ?.takeIf { it.startsWith(OPARL_SCHEMA_PREFIX) }
                    ?: parentSchema

                node.properties().forEach { (property, value) ->
                    val objectType = EMBEDDED_OBJECT_TYPES[property]
                    if (objectType != null && value is ObjectNode && value.textValue("type") == null) {
                        value.put("type", "${schema ?: OPARL_1_1_SCHEMA}/$objectType")
                    }
                    if (objectType != null && value is ArrayNode) {
                        value
                            .filterIsInstance<ObjectNode>()
                            .filter { it.textValue("type") == null }
                            .forEach { it.put("type", "${schema ?: OPARL_1_1_SCHEMA}/$objectType") }
                    }
                    addMissingEmbeddedTypes(value, schema)
                }
            }

            is ArrayNode -> node.forEach { addMissingEmbeddedTypes(it, parentSchema) }
        }
    }

    private fun JsonNode.textValue(property: String): String? {
        return get(property)
            ?.takeIf { it.isString }
            ?.asString()
    }

    private companion object {
        const val OPARL_SCHEMA_PREFIX = "https://schema.oparl.org/"
        const val OPARL_1_1_SCHEMA = "${OPARL_SCHEMA_PREFIX}1.1"

        val OPARL_VERSION_PATTERN = Regex("/v(1\\.0|1\\.1)(?:/|$)")

        val LIST_RESOURCE_TYPES = mapOf(
            "agendaitem" to "AgendaItem",
            "agendaitems" to "AgendaItem",
            "body" to "Body",
            "bodies" to "Body",
            "consultation" to "Consultation",
            "consultations" to "Consultation",
            "file" to "File",
            "files" to "File",
            "legislativeterm" to "LegislativeTerm",
            "legislativeterms" to "LegislativeTerm",
            "location" to "Location",
            "locations" to "Location",
            "meeting" to "Meeting",
            "meetings" to "Meeting",
            "membership" to "Membership",
            "memberships" to "Membership",
            "organization" to "Organization",
            "organizations" to "Organization",
            "paper" to "Paper",
            "papers" to "Paper",
            "person" to "Person",
            "persons" to "Person",
            "system" to "System",
            "systems" to "System",
        )

        val EMBEDDED_OBJECT_TYPES = mapOf(
            "agendaItem" to "AgendaItem",
            "auxiliaryFile" to "File",
            "consultation" to "Consultation",
            "invitation" to "File",
            "legislativeTerm" to "LegislativeTerm",
            "location" to "Location",
            "locationObject" to "Location",
            "mainFile" to "File",
            "membership" to "Membership",
            "resolutionFile" to "File",
            "resultsProtocol" to "File",
            "verbatimProtocol" to "File",
        )
    }
}
