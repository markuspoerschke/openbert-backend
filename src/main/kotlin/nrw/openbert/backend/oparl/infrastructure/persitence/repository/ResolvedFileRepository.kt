package nrw.openbert.backend.oparl.infrastructure.persitence.repository

import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedFile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ResolvedFileRepository : JpaRepository<ResolvedFile, UUID> {
    fun findAllByResource_System_IdOrderById(systemId: UUID): List<ResolvedFile>
}
