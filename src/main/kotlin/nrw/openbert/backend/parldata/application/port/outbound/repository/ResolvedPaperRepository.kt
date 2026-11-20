package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedPaper
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional
import java.util.UUID

interface ResolvedPaperRepository :
    JpaRepository<ResolvedPaper, UUID>,
    JpaSpecificationExecutor<ResolvedPaper> {
    fun findByResource(resource: OparlResource): ResolvedPaper?

    fun findByIdAndBodyId(
        id: UUID,
        bodyId: UUID,
    ): Optional<ResolvedPaper>
}
