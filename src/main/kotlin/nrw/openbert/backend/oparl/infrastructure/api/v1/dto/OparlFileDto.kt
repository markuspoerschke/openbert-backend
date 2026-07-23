package nrw.openbert.backend.oparl.infrastructure.api.v1.dto

import com.fasterxml.jackson.annotation.JsonInclude

data class OparlFileDto(
    val id: String,
    val type: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val name: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val fileName: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val mimeType: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val date: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val size: Int? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val sha1Checksum: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val sha512Checksum: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val text: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val accessUrl: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val downloadUrl: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val externalServiceUrl: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val fileLicense: String? = null,
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
