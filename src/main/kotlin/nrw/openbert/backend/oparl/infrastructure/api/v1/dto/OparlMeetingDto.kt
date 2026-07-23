package nrw.openbert.backend.oparl.infrastructure.api.v1.dto

import com.fasterxml.jackson.annotation.JsonInclude

data class OparlMeetingDto(
    val id: String,
    val type: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val name: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val meetingState: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val cancelled: Boolean? = null,

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
)
