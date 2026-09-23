package nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto

data class OparlListDto(
    val id: String? = null,
    val type: String? = null,
    val data: List<OparlObjectDto>? = null,
    val pagination: OparlListPaginationDto? = null,
    val links: OparlListLinksDto? = null,
) : OparlResponseDto
