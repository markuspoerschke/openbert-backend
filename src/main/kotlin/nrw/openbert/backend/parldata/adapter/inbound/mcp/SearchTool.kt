package nrw.openbert.backend.parldata.adapter.inbound.mcp

import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlSearchResultMapper
import nrw.openbert.backend.parldata.application.port.inbound.OparlEntitySearchServiceInterface
import nrw.openbert.backend.parldata.application.port.model.SearchCriteria
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
        @McpToolParam(required = true, description = "The search term")
        term: String,
    ) = searchService
        .search(SearchCriteria(term = term.trim()), PageRequest.of(0, 30))
        .map(mapper::toDto)
        .toList()
}
