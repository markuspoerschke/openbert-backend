package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.oparl.domain.entity.AbstractMembership
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawMembership

@Entity(name = "oparl_resolved_membership")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class ResolvedMembership(
    resource: OparlResource,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "body_id", nullable = false, updatable = false)
    val body: ResolvedBody,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false, updatable = false)
    val person: ResolvedPerson,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false, updatable = false)
    val organization: ResolvedOrganization,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "on_behalf_of_id", updatable = false)
    val onBehalfOf: ResolvedOrganization? = null,
) : AbstractMembership(resource) {
    companion object {
        fun fromRaw(
            raw: RawMembership,
            body: ResolvedBody,
            person: ResolvedPerson,
            organization: ResolvedOrganization,
            onBehalfOf: ResolvedOrganization? = null,
        ): ResolvedMembership {
            return ResolvedMembership(
                raw.resource,
                body,
                person,
                organization,
                onBehalfOf,
            ).apply { applyValuesFrom(raw) }
        }
    }
}
