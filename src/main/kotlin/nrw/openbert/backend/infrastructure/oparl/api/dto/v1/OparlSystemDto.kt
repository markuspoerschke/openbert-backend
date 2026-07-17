package nrw.openbert.backend.infrastructure.oparl.api.dto.v1

import com.fasterxml.jackson.annotation.JsonInclude

data class OparlSystemDto(
    val id: String,
    val type: String,
    val oparlVersion: String,
    val body: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val otherOparlVersions: List<String>? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val license: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val name: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val contactEmail: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val contactName: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val website: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val vendor: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val product: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val created: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val modified: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val web: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val deleted: Boolean? = null,
)
