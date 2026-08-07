package nrw.openbert.backend.oparl.domain.entity.raw

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.oparl.domain.entity.AbstractConsultation
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import nrw.openbert.backend.oparl.infrastructure.persistence.converter.OparlUrlConverter

@Entity(name = "oparl_raw_consultation")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class RawConsultation(resource: OparlResource) : AbstractConsultation(resource) {
    @Convert(converter = OparlUrlConverter::class)
    @Column(name = "paper_url", length = OparlUrl.MAX_LENGTH)
    var paperUrl: OparlUrl? = null

    @Convert(converter = OparlUrlConverter::class)
    @Column(name = "agenda_item_url", length = OparlUrl.MAX_LENGTH)
    var agendaItemUrl: OparlUrl? = null

    @Convert(converter = OparlUrlConverter::class)
    @Column(name = "meeting_url", length = OparlUrl.MAX_LENGTH)
    var meetingUrl: OparlUrl? = null

    @Column(name = "organization_urls", columnDefinition = "text[]")
    var organizationUrls: MutableList<String> = mutableListOf()
}
