package nrw.openbert.backend.oparl.domain.repository.raw

import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawBody
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RawBodyRepository : JpaRepository<RawBody, UUID> {
    fun findByResource(resource: OparlResource): RawBody?
}
