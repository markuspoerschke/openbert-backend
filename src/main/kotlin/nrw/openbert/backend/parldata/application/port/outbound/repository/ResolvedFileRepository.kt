package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ResolvedFileRepository : JpaRepository<ResolvedFile, UUID> {
    fun findByResource(resource: OparlResource): ResolvedFile?

    fun findAllByBody_IdOrderById(bodyId: UUID, pageable: Pageable): Page<ResolvedFile>

    fun findByIdAndBody_Id(id: UUID, bodyId: UUID): Optional<ResolvedFile>
}
