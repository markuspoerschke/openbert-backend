package nrw.openbert.backend.parldata.adapter.inbound.api.dto

import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.data.domain.PageRequest

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
    @Parameter(description = "1-based page number as required by OParl.")
    val page: Int = 1,
    /**
     * Number of elements per page.
     */
    @field:Min(1)
    @field:Max(MAX_PAGE_SIZE)
    @Parameter(description = "Number of elements per page.")
    val size: Int = PAGE_SIZE,
) {
    /**
     * Spring Data expects a 0-based page number, therefore the OParl page number is shifted by one.
     */
    fun toSpringPageRequest(): PageRequest = PageRequest.of(page - 1, size)

    companion object {
        const val PAGE_SIZE = 25
        const val MAX_PAGE_SIZE = 100L
    }
}
