package nrw.openbert.backend.parldata.adapter.inbound.api.dto

import jakarta.validation.constraints.Min
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

/**
 * Query parameters shared by all paginated OParl list endpoints.
 *
 * Validation lives here so that controllers only need to annotate the parameter with `@Valid`.
 */
data class OparlPaginationRequestDto(
    /**
     * 1-based page number as required by OParl.
     */
    @field:Min(1)
    val page: Int = 1,
) {
    /**
     * Spring Data expects a 0-based page number, therefore the OParl page number is shifted by one.
     */
    fun toPageable(): Pageable = PageRequest.of(page - 1, PAGE_SIZE)

    companion object {
        const val PAGE_SIZE = 25
    }
}
