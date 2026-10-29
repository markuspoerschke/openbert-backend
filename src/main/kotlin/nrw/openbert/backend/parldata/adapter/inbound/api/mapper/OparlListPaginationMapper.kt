package nrw.openbert.backend.parldata.adapter.inbound.api.mapper

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListPaginationDto
import nrw.openbert.backend.parldata.domain.extension.numberOneBased
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class OparlListPaginationMapper {
    fun toDto(page: Page<*>): OparlListPaginationDto =
        OparlListPaginationDto(
            page.totalElements.toInt(),
            page.size,
            page.numberOneBased,
            page.totalPages,
        )
}
