package nrw.openbert.backend.oparl.domain.entity.raw

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.oparl.domain.entity.AbstractOrganization
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_raw_organization")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class RawOrganization(resource: OparlResource) : AbstractOrganization(resource)
