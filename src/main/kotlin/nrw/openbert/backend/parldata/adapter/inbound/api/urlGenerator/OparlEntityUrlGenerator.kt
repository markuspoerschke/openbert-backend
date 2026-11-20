package nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import java.time.format.DateTimeFormatter
import java.util.UUID

class OparlEntityUrlGenerator(
    private val controller: Class<*>,
) {
    fun list(
        bodyId: UUID,
        request: OparlPaginationRequestDto = OparlPaginationRequestDto(),
    ): String = paginatedList(controller, "list", request, bodyId)

    fun get(
        bodyId: UUID,
        entityId: UUID,
    ): String =
        MvcUriComponentsBuilder
            .fromMethodName(controller, "get", bodyId, entityId)
            .build()
            .toString()
}

/**
 * [MvcUriComponentsBuilder] only renders `@PathVariable` and `@RequestParam` arguments. The request DTO is
 * passed for method matching only, its values have to be appended as query parameters explicitly. Sort and
 * filter parameters are kept so that the OParl `links` of a sorted/filtered list point to sorted/filtered pages.
 */
internal fun paginatedList(
    controller: Class<*>,
    methodName: String,
    request: OparlPaginationRequestDto,
    vararg pathVariables: Any,
): String {
    val builder =
        MvcUriComponentsBuilder
            .fromMethodName(controller, methodName, *pathVariables, request)
            .queryParam("page", request.page)

    if (request.size != OparlPaginationRequestDto.PAGE_SIZE) {
        builder.queryParam("size", request.size)
    }
    request.sort?.let { builder.queryParam("sort", it) }
    request.createdSince?.let { builder.queryParam("created_since", DATE_TIME.format(it)) }
    request.createdUntil?.let { builder.queryParam("created_until", DATE_TIME.format(it)) }
    request.modifiedSince?.let { builder.queryParam("modified_since", DATE_TIME.format(it)) }
    request.modifiedUntil?.let { builder.queryParam("modified_until", DATE_TIME.format(it)) }

    return builder.build().encode().toString()
}

private val DATE_TIME: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
