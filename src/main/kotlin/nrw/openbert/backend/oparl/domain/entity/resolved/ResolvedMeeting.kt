package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.oparl.domain.entity.AbstractMeeting
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawMeeting

@Entity(name = "oparl_resolved_meeting")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class ResolvedMeeting(
    resource: OparlResource,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "body_id", nullable = false, updatable = false)
    val body: ResolvedBody,
) : AbstractMeeting(resource) {
    @OneToMany(mappedBy = "meeting")
    @OrderBy("order ASC, id ASC")
    val agendaItems: MutableList<ResolvedAgendaItem> = mutableListOf()

    companion object {
        fun fromRaw(raw: RawMeeting, body: ResolvedBody): ResolvedMeeting {
            return ResolvedMeeting(raw.resource, body).apply { applyValuesFrom(raw) }
        }
    }
}
