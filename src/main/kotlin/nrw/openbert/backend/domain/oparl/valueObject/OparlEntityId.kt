package nrw.openbert.backend.domain.oparl.valueObject

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Embeddable
import nrw.openbert.backend.infrastructure.oparl.persitence.converter.OparlUrlConverter
import java.io.Serializable
import java.util.UUID

@Embeddable
data class OparlEntityId(
    @Column(updatable = false)
    val systemId: UUID,

    @Convert(converter = OparlUrlConverter::class)
    @Column(updatable = false, length = OparlUrl.MAX_LENGTH)
    val url: OparlUrl
) : Serializable
