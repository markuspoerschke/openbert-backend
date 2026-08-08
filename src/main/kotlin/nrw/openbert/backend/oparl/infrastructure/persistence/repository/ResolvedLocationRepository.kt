package nrw.openbert.backend.oparl.infrastructure.persistence.repository

import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedLocation
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ResolvedLocationRepository : JpaRepository<ResolvedLocation, UUID> {
    fun findByResource(resource: OparlResource): ResolvedLocation?

    fun findAllByBody_IdOrderById(bodyId: UUID): List<ResolvedLocation>

    fun findByIdAndBody_Id(id: UUID, bodyId: UUID): Optional<ResolvedLocation>
}
