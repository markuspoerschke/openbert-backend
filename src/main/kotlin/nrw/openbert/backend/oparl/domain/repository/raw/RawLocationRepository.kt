package nrw.openbert.backend.oparl.domain.repository.raw

import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawLocation
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RawLocationRepository : JpaRepository<RawLocation, UUID> {
    fun findByResource(resource: OparlResource): RawLocation?
}
