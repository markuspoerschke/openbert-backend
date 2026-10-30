package nrw.openbert.backend.parldata.adapter.inbound.mcp

import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlSearchResultMapper
import nrw.openbert.backend.parldata.application.port.inbound.OparlEntitySearchServiceInterface
import nrw.openbert.backend.parldata.application.port.model.SearchCriteria
import nrw.openbert.backend.parldata.domain.enums.OparlType
import org.springframework.ai.mcp.annotation.McpTool
import org.springframework.ai.mcp.annotation.McpToolParam
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class SearchTool(
    private val searchService: OparlEntitySearchServiceInterface,
    private val mapper: OparlSearchResultMapper,
) {
    @McpTool(
        name = "oparl_search",
        description =
            "Searches for content in the OParl system. " +
                "A search term should contain at least three consecutive letters or digits. " +
                "Never fails for a valid call: unknown type filter values are ignored and an unknown " +
                "body filter simply matches no results.",
    )
    fun search(
        @McpToolParam(required = false, description = "The search term")
        term: String? = null,
        @McpToolParam(required = false, description = "List of body IDs to filter by")
        bodies: List<String>? = null,
        @McpToolParam(
            required = false,
            description = "List of OParl resource types to filter",
        )
        types: List<OparlType>? = null,
    ) = searchService
        .search(
            SearchCriteria(
                term = term?.trim(),
                bodies = bodies ?: emptyList(),
                types = types ?: OparlType.entries.toList(),
            ),
            PageRequest.of(0, PAGE_SIZE),
        ).map(mapper::toDto)
        .toList()

    private companion object {
        const val PAGE_SIZE = 30
    }
}
