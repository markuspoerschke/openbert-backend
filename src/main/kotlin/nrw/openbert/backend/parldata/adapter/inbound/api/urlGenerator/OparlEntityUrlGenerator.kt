package nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import java.util.UUID

class OparlEntityUrlGenerator(
    private val controller: Class<*>,
) {
    fun list(
        bodyId: UUID,
        page: Int = 1,
        size: Int = OparlPaginationRequestDto.PAGE_SIZE,
    ): String = paginatedList(controller, "list", page, size, bodyId)

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
 * [MvcUriComponentsBuilder] only renders `@PathVariable` and `@RequestParam` arguments. The pagination DTO is
 * passed for method matching only, its `page` value has to be appended as query parameter explicitly.
 */
internal fun paginatedList(
    controller: Class<*>,
    methodName: String,
    page: Int,
    size: Int = OparlPaginationRequestDto.PAGE_SIZE,
    vararg pathVariables: Any,
): String {
    val builder =
        MvcUriComponentsBuilder
            .fromMethodName(controller, methodName, *pathVariables, OparlPaginationRequestDto(page, size))
            .queryParam("page", page)

    if (size != OparlPaginationRequestDto.PAGE_SIZE) {
        builder.queryParam("size", size)
    }

    return builder.build().toString()
}
