package nrw.openbert.backend.infrastructure.oparl.api.dto.v1

import com.fasterxml.jackson.annotation.JsonInclude

data class OparlBodyDto(
    val id: String,
    val type: String,
    val name: String,
    val organization: String,
    val person: String,
    val meeting: String,
    val paper: String,
    val legislativeTerm: List<OparlLegislativeTermDto>,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val system: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val shortName: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val website: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val license: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val licenseValidSince: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val oparlSince: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val ags: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val rgs: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val equivalent: List<String>? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val contactEmail: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val contactName: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val agendaItem: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val consultation: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val file: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val locationList: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val legislativeTermList: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val membership: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val classification: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val location: OparlLocationDto? = null,

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