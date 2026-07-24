package nrw.openbert.backend.oparl.domain.entity.embeddable

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Embeddable
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import nrw.openbert.backend.oparl.domain.entity.OparlSystem
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import nrw.openbert.backend.oparl.infrastructure.persistence.converter.OparlUrlConverter

@Embeddable
data class OparlResource(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "system_id", nullable = false, updatable = false)
    val system: OparlSystem,

    @Convert(converter = OparlUrlConverter::class)
    @Column(length = OparlUrl.MAX_LENGTH, nullable = false, updatable = false)
    val url: OparlUrl,
)
