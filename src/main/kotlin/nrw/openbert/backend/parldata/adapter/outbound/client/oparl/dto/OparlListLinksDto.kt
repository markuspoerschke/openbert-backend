package nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class OparlListLinksDto(
    val first: String? = null,
    val prev: String? = null,
    val self: String? = null,
    val next: String? = null,
    val last: String? = null,
    val web: String? = null,
)
