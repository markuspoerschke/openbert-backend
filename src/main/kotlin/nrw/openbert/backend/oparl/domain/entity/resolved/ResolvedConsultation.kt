package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.OrderColumn
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.oparl.domain.entity.AbstractConsultation
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawConsultation

@Entity(name = "oparl_resolved_consultation")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class ResolvedConsultation(
    resource: OparlResource,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "body_id", nullable = false, updatable = false)
    val body: ResolvedBody,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paper_id", nullable = false, updatable = false)
    val paper: ResolvedPaper,

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agenda_item_id", nullable = false, updatable = false, unique = true)
    val agendaItem: ResolvedAgendaItem,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meeting_id", nullable = false, updatable = false)
    val meeting: ResolvedMeeting,
) : AbstractConsultation(resource) {
    @ManyToMany
    @JoinTable(
        name = "oparl_resolved_consultation_organization",
        joinColumns = [JoinColumn(name = "consultation_id")],
        inverseJoinColumns = [JoinColumn(name = "organization_id")],
    )
    @OrderColumn(name = "organization_order")
    val organizations: MutableList<ResolvedOrganization> = mutableListOf()

    companion object {
        fun fromRaw(
            raw: RawConsultation,
            body: ResolvedBody,
            paper: ResolvedPaper,
            agendaItem: ResolvedAgendaItem,
            meeting: ResolvedMeeting,
            organizations: Collection<ResolvedOrganization> = emptyList(),
        ): ResolvedConsultation {
            return ResolvedConsultation(
                raw.resource,
                body,
                paper,
                agendaItem,
                meeting,
            ).apply {
                applyValuesFrom(raw)
                this.organizations.addAll(organizations)
            }
        }
    }
}
