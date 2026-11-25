package nrw.openbert.backend.parldata.adapter.inbound.api.dto

import io.swagger.v3.oas.annotations.media.Schema

data class OparlBodyStatisticsDto(
    @field:Schema(description = "Number of persons", minimum = "0", example = "0")
    val persons: Long,
    @field:Schema(description = "Number of organizations, including former organizations", minimum = "0", example = "0")
    val organizations: Long,
    @field:Schema(description = "Number of papers", minimum = "0", example = "0")
    val paper: Long,
    @field:Schema(description = "Number of file records", minimum = "0", example = "0")
    val files: Long,
    @field:Schema(description = "Number of meetings", minimum = "0", example = "0")
    val meetings: Long,
    @field:Schema(description = "Number of agenda items", minimum = "0", example = "0")
    val agendaItems: Long,
    @field:Schema(description = "Number of locations", minimum = "0", example = "0")
    val locations: Long,
    @field:Schema(description = "Number of consultations", minimum = "0", example = "0")
    val consultations: Long,
    @field:Schema(description = "Number of legislative terms", minimum = "0", example = "0")
    val legislativeTerms: Long,
    @field:Schema(description = "Number of memberships", minimum = "0", example = "0")
    val memberships: Long,
)
