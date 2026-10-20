package nrw.openbert.backend.parldata.adapter.inbound.mcp

import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlResourceMapper
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.domain.valueObject.OparlSearchReference
import org.springframework.ai.mcp.annotation.McpTool
import org.springframework.ai.mcp.annotation.McpToolParam
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.UUID

@Service
class ResourceTool(
    private val mapper: OparlResourceMapper,
) {
    @McpTool(
        name = "oparl_get_resource",
        description = "Retrieves one OParl resource by its full id URL from oparl_search, oparl_body_list, " +
            "or a resource relationship. Pass the id unchanged; no separate type or body ID is needed. " +
            "Supports bodies, legislative terms, organizations, people, memberships, meetings, agenda items, " +
            "papers, consultations, files and locations. List and file download URLs are not resource IDs.",
        annotations = McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, openWorldHint = false),
    )
    @Transactional(readOnly = true)
    fun getResource(
        @McpToolParam(required = true, description = "The full id URL returned by search or another OParl resource")
        id: String,
    ): Any {
        val baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()
        val path = id.takeIf { it.startsWith("$baseUrl/") }?.removePrefix(baseUrl)
        val match = requireNotNull(path?.let(RESOURCE_PATH::matchEntire)) {
            "Invalid resource ID. Pass a full local resource id URL from oparl_search or oparl_body_list unchanged."
        }
        val bodyId = UUID.fromString(match.groupValues[1])
        val typePath = match.groupValues[2]
        if (typePath.isEmpty()) {
            return mapper.toDto(OparlSearchReference(OparlType.BODY, bodyId))
        }
        val type = requireNotNull(RESOURCE_TYPES[typePath]) { "Unsupported OParl resource type: $typePath" }
        val entityId = UUID.fromString(match.groupValues[3])
        return mapper.toDto(OparlSearchReference(type, entityId), bodyId)
    }

    companion object {
        private const val UUID_PATTERN = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
        private val RESOURCE_PATH = Regex("/oparl/v1\\.1/body/($UUID_PATTERN)(?:/([a-z]+)/($UUID_PATTERN))?")
        private val RESOURCE_TYPES = OparlType.entries
            .filter { it != OparlType.SYSTEM && it != OparlType.BODY }
            .associateBy { it.name.lowercase().replace("_", "") }
    }
}
