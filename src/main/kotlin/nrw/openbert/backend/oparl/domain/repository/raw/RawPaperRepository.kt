package nrw.openbert.backend.oparl.domain.repository.raw

import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawPaper
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RawPaperRepository : JpaRepository<RawPaper, UUID> {
    fun findByResource(resource: OparlResource): RawPaper?
}
