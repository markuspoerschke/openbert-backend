package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.oparl.domain.entity.AbstractAgendaItem
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawAgendaItem

@Entity(name = "oparl_resolved_agendaItem")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class ResolvedAgendaItem(
    resource: OparlResource,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "body_id", nullable = false, updatable = false)
    val body: ResolvedBody,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meeting_id", nullable = false, updatable = false)
    val meeting: ResolvedMeeting,
) : AbstractAgendaItem(resource) {
    @OneToOne(mappedBy = "agendaItem")
    var consultation: ResolvedConsultation? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolution_file_id")
    var resolutionFile: ResolvedFile? = null
        protected set

    @ManyToMany
    @JoinTable(
        name = "oparl_resolved_agenda_item_auxiliary_file",
        joinColumns = [JoinColumn(name = "agenda_item_id")],
        inverseJoinColumns = [JoinColumn(name = "file_id")],
    )
    @OrderBy("id ASC")
    val auxiliaryFiles: MutableList<ResolvedFile> = mutableListOf()

    companion object {
        fun fromRaw(
            raw: RawAgendaItem,
            body: ResolvedBody,
            meeting: ResolvedMeeting,
            resolutionFile: ResolvedFile? = null,
            auxiliaryFiles: Collection<ResolvedFile> = emptyList(),
        ): ResolvedAgendaItem {
            return ResolvedAgendaItem(raw.resource, body, meeting).apply {
                applyValuesFrom(raw)
                this.resolutionFile = resolutionFile
                this.auxiliaryFiles.addAll(auxiliaryFiles)
            }
        }
    }
}
