package nrw.openbert.backend.oparl.domain.repository.raw

import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawFile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RawFileRepository : JpaRepository<RawFile, UUID> {
    fun findByResource(resource: OparlResource): RawFile?
}
