package nrw.openbert.backend.crawl.infrastructure.dto.oparl

data class OparlListDto(
    val id: String? = null,
    val type: String? = null,
    val data: List<OparlObjectDto>? = null,
    val pagination: OparlListPaginationDto? = null,
    val links: OparlListLinksDto? = null,
) : OparlResponseDto
