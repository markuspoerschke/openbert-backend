package nrw.openbert.backend.parldata.adapter.inbound.mcp

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListPaginationDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlSearchResultMapper
import nrw.openbert.backend.parldata.adapter.inbound.mcp.dto.McpSearchResultListDto
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
        @McpToolParam(
            required = false,
            description = "Page number to return (1-based)",
        )
        page: Int? = null,
        @McpToolParam(
            required = false,
            description = "Number of results per page",
        )
        pageSize: Int? = null,
    ): McpSearchResultListDto {
        val effectivePage = (page ?: 1).coerceAtLeast(1) - 1 // Convert to 0-based
        val effectivePageSize = (pageSize ?: DEFAULT_PAGE_SIZE).coerceAtLeast(1).coerceAtMost(MAX_PAGE_SIZE)

        val resultPage =
            searchService.search(
                SearchCriteria(
                    term = term?.trim(),
                    bodies = bodies ?: emptyList(),
                    types = types ?: OparlType.entries.toList(),
                ),
                PageRequest.of(effectivePage, effectivePageSize),
            )

        val pagination =
            OparlListPaginationDto(
                totalElements = Math.toIntExact(resultPage.totalElements),
                elementsPerPage = effectivePageSize,
                currentPage = effectivePage + 1, // Convert back to 1-based
                totalPages = resultPage.totalPages,
            )

        return McpSearchResultListDto(
            data = resultPage.map(mapper::toDto).toList(),
            pagination = pagination,
        )
    }

    private companion object {
        const val DEFAULT_PAGE_SIZE = 30
        const val MAX_PAGE_SIZE = 100
    }
}
