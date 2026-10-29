package nrw.openbert.backend.parldata.domain.entity.raw

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.parldata.domain.entity.AbstractBody
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource

@Entity(name = "oparl_raw_body")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class RawBody(
    resource: OparlResource,
) : AbstractBody(resource)
