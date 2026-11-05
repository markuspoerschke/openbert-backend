package nrw.openbert.backend.parldata.test.entityBuilder

import nrw.openbert.backend.parldata.domain.entity.OparlSystem
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import java.util.UUID

class OparlSystemBuilder {
    fun build() = OparlSystem(
        OparlUrl("https://oparl.example.org/webservice/oparl/v1.0/system"),
        UUID.fromString("77dd4a48-fb2d-4b02-afc4-ebe62a13bcb3")
    )
}
