package nrw.openbert.backend.parldata.application.port.model

import nrw.openbert.backend.parldata.domain.enums.OparlType

data class SearchCriteria(
    val term: String? = null,
    val bodies: List<String> = emptyList(),
    val types: List<OparlType> = OparlType.entries.toList(),
)
