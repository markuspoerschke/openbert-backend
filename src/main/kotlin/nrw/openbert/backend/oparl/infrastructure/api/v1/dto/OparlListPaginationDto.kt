package nrw.openbert.backend.oparl.infrastructure.api.v1.dto

data class OparlListPaginationDto(
    val totalElements: Int,
    val elementsPerPage: Int,
    val currentPage: Int,
    val totalPages: Int,
)
