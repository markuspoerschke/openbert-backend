package nrw.openbert.backend.parldata.domain.entity.raw

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.parldata.domain.entity.AbstractAgendaItem
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl

@Entity(name = "oparl_raw_agendaItem")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class RawAgendaItem(
    resource: OparlResource,
) : AbstractAgendaItem(resource) {
    @Column(name = "meeting_url", length = OparlUrl.MAX_LENGTH)
    var meetingUrl: OparlUrl? = null

    @Column(name = "consultation_url", length = OparlUrl.MAX_LENGTH)
    var consultationUrl: OparlUrl? = null

    @Column(name = "resolution_file_url", length = OparlUrl.MAX_LENGTH)
    var resolutionFileUrl: OparlUrl? = null

    @Column(name = "auxiliary_file_urls", columnDefinition = "text[]")
    var auxiliaryFileUrls: MutableList<String> = mutableListOf()
}
