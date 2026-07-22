package nrw.openbert.backend.oparl.infrastructure.api.v1.dto

import com.fasterxml.jackson.annotation.JsonInclude

data class OparlLocationDto(
    val id: String,
    val type: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val description: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val geojson: Map<String, Any?>? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val streetAddress: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val room: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val postalCode: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val subLocality: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val locality: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val bodies: List<String>? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val organizations: List<String>? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val persons: List<String>? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val meetings: List<String>? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val papers: List<String>? = null,

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
)