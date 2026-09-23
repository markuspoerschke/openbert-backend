package nrw.openbert.backend.parldata.domain.entity.raw

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.parldata.domain.entity.AbstractFile
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource

@Entity(name = "oparl_raw_file")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class RawFile(resource: OparlResource) : AbstractFile(resource) {
    @Column(name = "meeting_urls", columnDefinition = "text[]")
    var meetingUrls: MutableList<String> = mutableListOf()

    @Column(name = "agenda_item_urls", columnDefinition = "text[]")
    var agendaItemUrls: MutableList<String> = mutableListOf()

    @Column(name = "paper_urls", columnDefinition = "text[]")
    var paperUrls: MutableList<String> = mutableListOf()
}
