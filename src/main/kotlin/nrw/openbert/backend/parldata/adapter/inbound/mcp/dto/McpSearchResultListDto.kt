package nrw.openbert.backend.parldata.adapter.inbound.mcp.dto

import com.fasterxml.jackson.annotation.JsonInclude
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListPaginationDto

/**
 * DTO for MCP search results with pagination support.
 * This is similar to OparlSearchResultListDto but without links and id field,
 * as they don't make sense in the MCP context.
 */
data class McpSearchResultListDto(
    val data: List<Any>,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val pagination: OparlListPaginationDto? = null,
)
