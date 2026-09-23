package nrw.openbert.backend.parldata.adapter.inbound.api.dto

data class OparlListPaginationDto(
    val totalElements: Int,
    val elementsPerPage: Int,
    val currentPage: Int,
    val totalPages: Int,
)
