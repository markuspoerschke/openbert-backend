package nrw.openbert.backend.parldata.domain.entity.raw

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.parldata.domain.entity.AbstractPerson
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import nrw.openbert.backend.parldata.adapter.outbound.persistence.converter.OparlUrlConverter

@Entity(name = "oparl_raw_person")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class RawPerson(resource: OparlResource) : AbstractPerson(resource) {
    @Convert(converter = OparlUrlConverter::class)
    @Column(name = "body_url", length = OparlUrl.MAX_LENGTH)
    var bodyUrl: OparlUrl? = null
}
