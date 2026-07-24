package nrw.openbert.backend.oparl.infrastructure.persistence.repository

import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedFile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ResolvedFileRepository : JpaRepository<ResolvedFile, UUID> {
    fun findAllByBody_IdOrderById(bodyId: UUID): List<ResolvedFile>

    fun findByIdAndBody_Id(id: UUID, bodyId: UUID): Optional<ResolvedFile>
}
