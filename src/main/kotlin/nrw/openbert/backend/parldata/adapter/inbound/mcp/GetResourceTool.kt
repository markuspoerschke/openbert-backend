package nrw.openbert.backend.parldata.adapter.inbound.mcp

import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlSearchResultMapper
import nrw.openbert.backend.parldata.domain.valueObject.OparlSearchReference
import org.springframework.ai.mcp.annotation.McpTool
import org.springframework.ai.mcp.annotation.McpToolParam
import org.springframework.stereotype.Service

@Service
class GetResourceTool(
    private val mapper: OparlSearchResultMapper,
) {
    @McpTool(
        name = "oparl_get_resource",
        description =
            "Fetches a single OParl resource (body, legislative term, organization, person, membership, " +
                "meeting, agenda item, paper, consultation, file, or location) by its ID. " +
                "Use the id value of a resource returned by the oparl_search or oparl_body_list tools.",
    )
    fun getResource(
        @McpToolParam(
            required = true,
            description =
                "The id of the OParl resource to fetch, " +
                    "e.g. https://example.org/oparl/v1.1/body/{bodyId}/person/{personId}",
        )
        id: String,
    ): Any {
        val reference =
            OparlResourceId.fromUrl(id)
                ?: throw IllegalArgumentException("Unsupported OParl resource id: $id")

        return mapper.toDto(OparlSearchReference(reference.type, reference.id))
    }
}
