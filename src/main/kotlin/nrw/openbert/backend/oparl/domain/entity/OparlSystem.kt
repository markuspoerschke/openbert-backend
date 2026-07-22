package nrw.openbert.backend.oparl.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Id
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import nrw.openbert.backend.oparl.infrastructure.persitence.converter.OparlUrlConverter
import java.util.UUID

@Entity(name = "oparl_system")
class OparlSystem(
    @Convert(converter = _root_ide_package_.nrw.openbert.backend.oparl.infrastructure.persitence.converter.OparlUrlConverter::class)
    @Column(nullable = false, updatable = false, length = OparlUrl.MAX_LENGTH)
    val url: OparlUrl,

    @Id
    @Column(nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID()
)