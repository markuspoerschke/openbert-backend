package nrw.openbert.backend.parldata.domain.entity.embeddable

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import nrw.openbert.backend.parldata.domain.entity.OparlSystem
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl

@Embeddable
data class OparlResource(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "system_id", nullable = false, updatable = false)
    val system: OparlSystem,

    @Column(length = OparlUrl.MAX_LENGTH, nullable = false, updatable = false)
    val url: OparlUrl,
)
