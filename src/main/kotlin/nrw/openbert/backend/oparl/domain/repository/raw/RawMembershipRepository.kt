package nrw.openbert.backend.oparl.domain.repository.raw

import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawMembership
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RawMembershipRepository : JpaRepository<RawMembership, UUID> {
    fun findByResource(resource: OparlResource): RawMembership?
}
