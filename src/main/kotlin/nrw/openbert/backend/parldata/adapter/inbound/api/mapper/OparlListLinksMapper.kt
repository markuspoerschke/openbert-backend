package nrw.openbert.backend.parldata.adapter.inbound.api.mapper

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListLinksDto
import nrw.openbert.backend.parldata.domain.extension.numberOneBased
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class OparlListLinksMapper {
    fun toDto(
        page: Page<*>, listUrlGenerator: (page: Int) -> String
    ): OparlListLinksDto {
        return OparlListLinksDto(
            first(listUrlGenerator),
            prev(page, listUrlGenerator),
            self(page, listUrlGenerator),
            next(page, listUrlGenerator),
            last(page, listUrlGenerator),
        )
    }

    private fun first(listUrlGenerator: (page: Int) -> String): String {
        return listUrlGenerator(1)
    }

    private fun prev(page: Page<*>, listUrlGenerator: (page: Int) -> String): String? {
        if (page.hasPrevious()) {
            return listUrlGenerator(page.numberOneBased - 1)
        }

        return null
    }

    private fun self(page: Page<*>, listUrlGenerator: (page: Int) -> String): String {
        return listUrlGenerator(page.numberOneBased)
    }

    private fun next(page: Page<*>, listUrlGenerator: (page: Int) -> String): String? {
        if (page.hasNext()) {
            return listUrlGenerator(page.numberOneBased + 1)
        }

        return null
    }

    private fun last(page: Page<*>, listUrlGenerator: (page: Int) -> String): String {
        return listUrlGenerator(page.totalPages)
    }
}
