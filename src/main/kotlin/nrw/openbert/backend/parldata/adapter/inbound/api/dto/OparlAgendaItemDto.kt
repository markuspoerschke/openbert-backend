package nrw.openbert.backend.parldata.adapter.inbound.api.dto

import com.fasterxml.jackson.annotation.JsonInclude

data class OparlAgendaItemDto(
    val id: String,
    val type: String,
    val meeting: String,
    val order: Int,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val number: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val name: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val public: Boolean? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val consultation: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val result: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val resolutionText: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val resolutionFile: OparlFileDto? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val auxiliaryFile: List<OparlFileDto>? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val start: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val end: String? = null,

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
        const val TYPE = "https://schema.oparl.org/1.1/AgendaItem"
    }
}
