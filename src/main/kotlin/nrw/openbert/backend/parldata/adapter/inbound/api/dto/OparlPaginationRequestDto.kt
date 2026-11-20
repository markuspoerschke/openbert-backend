package nrw.openbert.backend.parldata.adapter.inbound.api.dto

import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.BindParam
import java.time.OffsetDateTime

/**
 * Query parameters shared by all paginated OParl list endpoints: pagination, the sort order, and the
 * OParl 1.1 filter parameters `created_since`, `created_until`, `modified_since`, `modified_until`.
 *
 * The OParl `limit` parameter is accepted but ignored, which the standard explicitly allows.
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
    @Parameter(description = "Sort as `<field>,<asc|desc>` (allowed: id, created, modified). Default: `id,desc`.")
    val sort: String? = null,
    @BindParam("created_since")
    @Parameter(description = "OParl filter: only objects created at or after this date-time.")
    val createdSince: OffsetDateTime? = null,
    @BindParam("created_until")
    @Parameter(description = "OParl filter: only objects created at or before this date-time.")
    val createdUntil: OffsetDateTime? = null,
    @BindParam("modified_since")
    @Parameter(description = "OParl filter: objects modified at or after this date-time, including deleted ones.")
    val modifiedSince: OffsetDateTime? = null,
    @BindParam("modified_until")
    @Parameter(description = "OParl filter: only objects modified at or before this date-time.")
    val modifiedUntil: OffsetDateTime? = null,
) {
    @get:AssertTrue(message = "Unsupported sort field; allowed: id, created, modified.")
    val isSortValid: Boolean
        get() = sort == null || sort.substringBefore(",").trim() in SORTABLE_PROPERTIES

    /**
     * Spring Data expects a 0-based page number, therefore the OParl page number is shifted by one.
     */
    fun toSpringPageRequest(): PageRequest = PageRequest.of(page - 1, size, toSpringSort())

    /**
     * Default is `id` descending (newest first). The unique id is always the final tiebreaker
     * so the list order stays stable across pages.
     */
    fun toSpringSort(): Sort {
        val order = sort?.toSortOrder() ?: Sort.Order.desc("id")
        val orders = if (order.property == "id") listOf(order) else listOf(order, Sort.Order.asc("id"))
        return Sort.by(orders)
    }

    private fun String.toSortOrder(): Sort.Order {
        val parts = split(",")
        val direction =
            if (parts.getOrNull(1)?.trim()?.equals("desc", ignoreCase = true) == true) {
                Sort.Direction.DESC
            } else {
                Sort.Direction.ASC
            }
        return Sort.Order(direction, parts[0].trim())
    }

    companion object {
        const val PAGE_SIZE = 25
        const val MAX_PAGE_SIZE = 100L
        val SORTABLE_PROPERTIES = setOf("id", "created", "modified")
    }
}
