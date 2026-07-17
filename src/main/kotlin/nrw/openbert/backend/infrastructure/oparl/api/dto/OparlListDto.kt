package nrw.openbert.backend.infrastructure.oparl.api.dto

import com.fasterxml.jackson.annotation.JsonInclude

data class OparlListDto<T>(
    val id: String,

    val type: String,

    val data: List<T>,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val pagination: OparlListPaginationDto? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val links: OparlListLinksDto? = null,
)
