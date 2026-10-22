package nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import java.util.UUID

class OparlEntityUrlGenerator(private val controller: Class<*>) {
    fun list(bodyId: UUID, page: Int = 1): String {
        return paginatedList(controller, "list", page, bodyId)
    }

    fun get(bodyId: UUID, entityId: UUID): String {
        return MvcUriComponentsBuilder
            .fromMethodName(controller, "get", bodyId, entityId)
            .build()
            .toString()
    }
}

/**
 * [MvcUriComponentsBuilder] only renders `@PathVariable` and `@RequestParam` arguments. The pagination DTO is
 * passed for method matching only, its `page` value has to be appended as query parameter explicitly.
 */
internal fun paginatedList(
    controller: Class<*>,
    methodName: String,
    page: Int,
    vararg pathVariables: Any,
): String {
    return MvcUriComponentsBuilder
        .fromMethodName(controller, methodName, *pathVariables, OparlPaginationRequestDto(page))
        .queryParam("page", page)
        .build()
        .toString()
}
