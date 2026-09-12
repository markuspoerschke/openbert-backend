package nrw.openbert.backend.oparl.infrastructure.api.v1.dto

import com.fasterxml.jackson.annotation.JsonInclude

data class OparlPaperDto(
    val id: String,
    val type: String,
    val body: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val name: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val reference: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val date: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val paperType: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val relatedPaper: List<String>? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val superordinatedPaper: List<String>? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val subordinatedPaper: List<String>? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val mainFile: OparlFileDto? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val auxiliaryFile: List<OparlFileDto>? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val location: List<OparlLocationDto>? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val originatorPerson: List<String>? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val underDirectionOf: List<String>? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val originatorOrganization: List<String>? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val consultation: List<OparlConsultationDto>? = null,

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
        const val TYPE = "https://schema.oparl.org/1.1/Paper"
    }
}
