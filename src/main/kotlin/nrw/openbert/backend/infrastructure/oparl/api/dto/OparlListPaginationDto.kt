package nrw.openbert.backend.infrastructure.oparl.api.dto

data class OparlListPaginationDto(
    val totalElements: Int,
    val elementsPerPage: Int,
    val currentPage: Int,
    val totalPages: Int,
)
