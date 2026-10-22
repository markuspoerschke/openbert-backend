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
    @McpTool(description = "Searches for content in the OParl system", name = "oparl_search")
    fun search(
        @McpToolParam(required = false, description = "The search term")
        term: String? = null,
        
        @McpToolParam(required = false, description = "List of body IDs to filter by")
        bodies: List<String>? = null,
        
        @McpToolParam(required = false, description = "List of OParl types to filter by (e.g., PERSON, ORGANIZATION)")
        types: List<String>? = null,
    ) = searchService
        .search(
            SearchCriteria(
                term = term?.trim(),
                bodies = bodies ?: emptyList(),
                types = types?.mapNotNull { typeString ->
                    try {
                        OparlType.valueOf(typeString.uppercase())
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                } ?: OparlType.entries.toList()
            ),
            PageRequest.of(0, 30)
        )
        .map(mapper::toDto)
        .toList()
}
