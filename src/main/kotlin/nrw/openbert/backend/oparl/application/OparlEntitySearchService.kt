package nrw.openbert.backend.oparl.application

import nrw.openbert.backend.oparl.infrastructure.api.v1.mapper.OparlSearchResultMapper
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.OparlSearchRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OparlEntitySearchService(
    private val searchRepository: OparlSearchRepository,
    private val searchResultMapper: OparlSearchResultMapper,
) {
    @Transactional(readOnly = true)
    fun search(text: String, pageable: Pageable): Page<Any> {
        return searchRepository.search(text, pageable).map(searchResultMapper::toDto)
    }
}
