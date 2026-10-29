package nrw.openbert.backend.parldata.adapter.inbound.api.dto

import com.fasterxml.jackson.annotation.JsonInclude

data class OparlListDto<T>(
    val id: String,
    val data: List<T>,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val pagination: OparlListPaginationDto? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val links: OparlListLinksDto? = null,
) {
    val type: String = TYPE

    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/List"
    }
}
