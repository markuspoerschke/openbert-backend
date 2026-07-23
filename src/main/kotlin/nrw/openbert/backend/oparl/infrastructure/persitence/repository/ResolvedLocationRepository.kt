package nrw.openbert.backend.oparl.infrastructure.persitence.repository

import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedLocation
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ResolvedLocationRepository : JpaRepository<ResolvedLocation, UUID> {
    fun findAllByBody_IdOrderById(bodyId: UUID): List<ResolvedLocation>
}
