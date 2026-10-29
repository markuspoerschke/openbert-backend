package nrw.openbert.backend.parldata.adapter.inbound.api.dto

import com.fasterxml.jackson.annotation.JsonInclude

data class OparlLegislativeTermDto(
    val id: String,
    val type: String,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val body: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val name: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val startDate: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val endDate: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val license: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val keyword: List<String>? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val created: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val modified: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val web: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val deleted: Boolean? = null,
) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/LegislativeTerm"
    }
}
