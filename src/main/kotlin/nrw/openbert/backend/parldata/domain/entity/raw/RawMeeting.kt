package nrw.openbert.backend.parldata.domain.entity.raw

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.parldata.domain.entity.AbstractMeeting
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource

@Entity(name = "oparl_raw_meeting")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class RawMeeting(resource: OparlResource) : AbstractMeeting(resource) {
    @Column(name = "organization_urls", columnDefinition = "text[]")
    var organizationUrls: MutableList<String> = mutableListOf()

    @Column(name = "participant_urls", columnDefinition = "text[]")
    var participantUrls: MutableList<String> = mutableListOf()
}
