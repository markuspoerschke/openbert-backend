package nrw.openbert.backend.parldata.application.port.inbound

import nrw.openbert.backend.parldata.adapter.outbound.persistence.repository.OparlSearchReference
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OparlEntitySearchServiceInterface {
    fun search(text: String, pageable: Pageable): Page<OparlSearchReference>
}
