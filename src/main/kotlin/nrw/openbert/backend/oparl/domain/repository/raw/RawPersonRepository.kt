package nrw.openbert.backend.oparl.domain.repository.raw

import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawPerson
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RawPersonRepository : JpaRepository<RawPerson, UUID> {
    fun findByResource(resource: OparlResource): RawPerson?
}
