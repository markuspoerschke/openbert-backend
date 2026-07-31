package nrw.openbert.backend.oparl.domain.repository.raw

import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawOrganization
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RawOrganizationRepository : JpaRepository<RawOrganization, UUID> {
    fun findByResource(resource: OparlResource): RawOrganization?
}
