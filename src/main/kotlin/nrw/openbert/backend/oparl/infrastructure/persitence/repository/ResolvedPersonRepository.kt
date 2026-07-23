package nrw.openbert.backend.oparl.infrastructure.persitence.repository

import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedPerson
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ResolvedPersonRepository : JpaRepository<ResolvedPerson, UUID> {
    fun findAllByBody_IdOrderById(bodyId: UUID): List<ResolvedPerson>
}
