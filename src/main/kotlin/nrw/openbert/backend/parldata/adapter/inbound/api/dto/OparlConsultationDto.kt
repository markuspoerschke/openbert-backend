package nrw.openbert.backend.parldata.adapter.inbound.api.dto

import com.fasterxml.jackson.annotation.JsonInclude

data class OparlConsultationDto(
    val id: String,
    val type: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val paper: String? = null,

    val agendaItem: String,
    val meeting: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val organization: List<String>? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val authoritative: Boolean? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val role: String? = null,

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
        const val TYPE = "https://schema.oparl.org/1.1/Consultation"
    }
}
