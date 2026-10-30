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
                "Use the id value of a resource returned by the oparl_search or oparl_body_list tools. " +
                "Fails with an error result when the id is not the URL of a supported OParl resource; " +
                "the error message then explains the expected URL format.",
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
                ?: throw IllegalArgumentException(unsupportedResourceIdMessage(id))

        return mapper.toDto(OparlSearchReference(reference.type, reference.id))
    }

    private fun unsupportedResourceIdMessage(id: String): String =
        "Unsupported OParl resource id '$id'. " +
            "Provide the URL of an OParl resource, for example '$EXAMPLE_URL'. " +
            "Supported resources are bodies with a URL ending in '/oparl/v1.1/body/{bodyId}' and the " +
            "body-scoped resources agendaitem, consultation, file, legislativeterm, location, meeting, " +
            "membership, organization, paper, and person."

    private companion object {
        private const val EXAMPLE_URL = "https://example.org/oparl/v1.1/body/{bodyId}/person/{personId}"
    }
}
