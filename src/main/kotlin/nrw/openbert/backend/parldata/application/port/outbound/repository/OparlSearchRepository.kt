package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.application.port.model.SearchCriteria
import nrw.openbert.backend.parldata.domain.valueObject.OparlSearchReference
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OparlSearchRepository {
    fun search(
        searchCriteria: SearchCriteria,
        pageable: Pageable,
    ): Page<OparlSearchReference>
}
