package nrw.openbert.backend.parldata.adapter.inbound.api.dto

import com.fasterxml.jackson.annotation.JsonInclude

data class OparlPersonDto(
    val id: String,
    val type: String,
    val body: String,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val name: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val familyName: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val givenName: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val formOfAddress: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val affix: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val title: List<String>? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val gender: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val phone: List<String>? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val email: List<String>? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val status: List<String>? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val membership: List<OparlMembershipDto>? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val life: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val lifeSource: String? = null,
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
        const val TYPE = "https://schema.oparl.org/1.1/Person"
    }
}
