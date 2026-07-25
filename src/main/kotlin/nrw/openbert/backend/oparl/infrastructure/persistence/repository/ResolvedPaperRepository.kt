package nrw.openbert.backend.oparl.infrastructure.persistence.repository

import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedPaper
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ResolvedPaperRepository : JpaRepository<ResolvedPaper, UUID> {
    fun findAllByBody_IdOrderById(bodyId: UUID): List<ResolvedPaper>

    fun findByIdAndBody_Id(id: UUID, bodyId: UUID): Optional<ResolvedPaper>
}
