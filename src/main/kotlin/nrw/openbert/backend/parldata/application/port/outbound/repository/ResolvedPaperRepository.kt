package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedPaper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ResolvedPaperRepository : JpaRepository<ResolvedPaper, UUID> {
    fun findByResource(resource: OparlResource): ResolvedPaper?

    fun findAllByBodyIdOrderById(
        bodyId: UUID,
        pageable: Pageable,
    ): Page<ResolvedPaper>

    fun findByIdAndBodyId(
        id: UUID,
        bodyId: UUID,
    ): Optional<ResolvedPaper>
}
