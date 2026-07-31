package nrw.openbert.backend.crawl.infrastructure.dto.oparl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class OparlListPaginationDto(
    val totalElements: Int? = null,
    val elementsPerPage: Int? = null,
    val currentPage: Int? = null,
    val totalPages: Int? = null,
)
