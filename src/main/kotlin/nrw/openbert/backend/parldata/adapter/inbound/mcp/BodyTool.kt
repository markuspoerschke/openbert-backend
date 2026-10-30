package nrw.openbert.backend.parldata.adapter.inbound.mcp

import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlBodyMapper
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import org.springframework.ai.mcp.annotation.McpTool
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class BodyTool(
    val repository: ResolvedBodyRepository,
    val mapper: OparlBodyMapper,
) {
    @McpTool(
        name = "oparl_body_list",
        description =
            "List all available bodies in the system. " +
                "Never fails for a valid call; returns an empty list when there are no bodies.",
    )
    fun listBodies() =
        repository
            .findAll(Sort.by(Sort.Direction.DESC, "name"))
            .map(mapper::toDto)
}
