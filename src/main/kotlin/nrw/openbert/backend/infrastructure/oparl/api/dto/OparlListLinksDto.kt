package nrw.openbert.backend.infrastructure.oparl.api.dto

import com.fasterxml.jackson.annotation.JsonInclude

data class OparlListLinksDto(
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val first: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val prev: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val self: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val next: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val last: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val web: String? = null,
)
