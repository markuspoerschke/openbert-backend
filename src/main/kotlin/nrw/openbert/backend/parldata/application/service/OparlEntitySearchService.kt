package nrw.openbert.backend.parldata.application.service

import nrw.openbert.backend.parldata.adapter.outbound.persistence.repository.OparlSearchRepository
import nrw.openbert.backend.parldata.application.port.inbound.OparlEntitySearchServiceInterface
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OparlEntitySearchService(
    private val searchRepository: OparlSearchRepository,
) : OparlEntitySearchServiceInterface {
    @Transactional(readOnly = true)
    override fun search(
        text: String,
        pageable: Pageable
    ) = searchRepository.search(text, pageable)
}
