package nrw.openbert.backend.parldata.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderColumn
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.parldata.domain.entity.AbstractMeeting
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawMeeting

@Entity(name = "oparl_resolved_meeting")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class ResolvedMeeting(
    resource: OparlResource,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "body_id", nullable = false, updatable = false)
    val body: ResolvedBody,
) : AbstractMeeting(resource) {
    @ManyToMany
    @JoinTable(
        name = "oparl_resolved_meeting_organization",
        joinColumns = [JoinColumn(name = "meeting_id")],
        inverseJoinColumns = [JoinColumn(name = "organization_id")],
    )
    @OrderColumn(name = "organization_order")
    val organizations: MutableList<ResolvedOrganization> = mutableListOf()

    @OneToMany(mappedBy = "meeting")
    @OrderBy("order ASC, id ASC")
    val agendaItems: MutableList<ResolvedAgendaItem> = mutableListOf()

    fun applyRelationsFrom(organizations: Collection<ResolvedOrganization>) {
        this.organizations.clear()
        this.organizations.addAll(organizations)
    }

    companion object {
        fun fromRaw(
            raw: RawMeeting,
            body: ResolvedBody,
            organizations: Collection<ResolvedOrganization> = emptyList(),
        ): ResolvedMeeting {
            return ResolvedMeeting(raw.resource, body).apply {
                applyValuesFrom(raw)
                applyRelationsFrom(organizations)
            }
        }
    }
}
