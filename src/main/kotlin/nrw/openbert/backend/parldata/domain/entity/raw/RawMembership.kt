package nrw.openbert.backend.parldata.domain.entity.raw

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.parldata.domain.entity.AbstractMembership
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import nrw.openbert.backend.parldata.adapter.outbound.persistence.converter.OparlUrlConverter

@Entity(name = "oparl_raw_membership")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class RawMembership(resource: OparlResource) : AbstractMembership(resource) {
    @Convert(converter = OparlUrlConverter::class)
    @Column(name = "person_url", length = OparlUrl.MAX_LENGTH)
    var personUrl: OparlUrl? = null

    @Convert(converter = OparlUrlConverter::class)
    @Column(name = "organization_url", length = OparlUrl.MAX_LENGTH)
    var organizationUrl: OparlUrl? = null

    @Convert(converter = OparlUrlConverter::class)
    @Column(name = "on_behalf_of_url", length = OparlUrl.MAX_LENGTH)
    var onBehalfOfUrl: OparlUrl? = null
}
