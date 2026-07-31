package nrw.openbert.backend.oparl.domain.repository.raw

import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawSystem
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RawSystemRepository : JpaRepository<RawSystem, UUID> {
    fun findByResource(resource: OparlResource): RawSystem?
}
