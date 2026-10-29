package nrw.openbert.backend.parldata.domain.entity.raw

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.parldata.domain.entity.AbstractLocation
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource

@Entity(name = "oparl_raw_location")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class RawLocation(
    resource: OparlResource,
) : AbstractLocation(resource) {
    @Column(name = "body_urls", columnDefinition = "text[]")
    var bodyUrls: MutableList<String> = mutableListOf()

    @Column(name = "organization_urls", columnDefinition = "text[]")
    var organizationUrls: MutableList<String> = mutableListOf()

    @Column(name = "person_urls", columnDefinition = "text[]")
    var personUrls: MutableList<String> = mutableListOf()

    @Column(name = "meeting_urls", columnDefinition = "text[]")
    var meetingUrls: MutableList<String> = mutableListOf()

    @Column(name = "paper_urls", columnDefinition = "text[]")
    var paperUrls: MutableList<String> = mutableListOf()
}
