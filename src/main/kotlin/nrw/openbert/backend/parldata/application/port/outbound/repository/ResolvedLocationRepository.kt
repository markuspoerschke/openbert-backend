package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedLocation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ResolvedLocationRepository : JpaRepository<ResolvedLocation, UUID> {
    fun findByResource(resource: OparlResource): ResolvedLocation?

    fun findAllByBodyIdOrderById(
        bodyId: UUID,
        pageable: Pageable,
    ): Page<ResolvedLocation>

    fun findByIdAndBodyId(
        id: UUID,
        bodyId: UUID,
    ): Optional<ResolvedLocation>
}
