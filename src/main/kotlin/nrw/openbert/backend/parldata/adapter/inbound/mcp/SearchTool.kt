package nrw.openbert.backend.parldata.adapter.inbound.mcp

import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlResourceMapper
import nrw.openbert.backend.parldata.application.port.inbound.OparlEntitySearchServiceInterface
import nrw.openbert.backend.parldata.application.port.model.SearchCriteria
import org.springframework.ai.mcp.annotation.McpTool
import org.springframework.ai.mcp.annotation.McpToolParam
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SearchTool(
    private val searchService: OparlEntitySearchServiceInterface,
    private val mapper: OparlResourceMapper,
) {
    @McpTool(
        description = "Searches for content in the OParl system. Pass a returned object's id unchanged to " +
            "oparl_get_resource to retrieve it or use IDs in its relationships to explore related resources.",
        name = "oparl_search",
        annotations = McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, openWorldHint = false),
    )
    @Transactional(readOnly = true)
    fun search(
        @McpToolParam(required = true, description = "The search term")
        term: String,
    ) = searchService
        .search(SearchCriteria(term = term.trim()), PageRequest.of(0, 30))
        .map { mapper.toDto(it) }
        .toList()
}
