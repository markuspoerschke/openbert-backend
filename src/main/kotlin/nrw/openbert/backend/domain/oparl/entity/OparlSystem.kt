package nrw.openbert.backend.domain.oparl.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Id
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId
import nrw.openbert.backend.domain.oparl.valueObject.OparlUrl
import nrw.openbert.backend.infrastructure.oparl.persitence.converter.OparlUrlConverter
import java.util.UUID

@Entity(name = "oparl_system")
class OparlSystem(
    @Convert(converter = OparlUrlConverter::class)
    @Column(nullable = false, updatable = false, length = OparlUrl.MAX_LENGTH)
    val url: OparlUrl,

    @Id
    @Column(nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID()
) {
    fun entityId(url: OparlUrl): OparlEntityId = OparlEntityId(id, url)
}