package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedPerson
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ResolvedPersonRepository : JpaRepository<ResolvedPerson, UUID> {
    fun findByResource(resource: OparlResource): ResolvedPerson?

    fun findAllByBody_IdOrderById(bodyId: UUID): List<ResolvedPerson>

    fun findByIdAndBody_Id(id: UUID, bodyId: UUID): Optional<ResolvedPerson>
}
