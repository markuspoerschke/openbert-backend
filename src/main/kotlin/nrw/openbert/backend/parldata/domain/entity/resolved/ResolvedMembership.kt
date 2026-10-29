package nrw.openbert.backend.parldata.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.parldata.domain.entity.AbstractMembership
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawMembership

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
    @JoinColumn(name = "on_behalf_of_id")
    var onBehalfOf: ResolvedOrganization? = null,
) : AbstractMembership(resource) {
    fun applyRelationsFrom(onBehalfOf: ResolvedOrganization?) {
        this.onBehalfOf = onBehalfOf
    }

    companion object {
        fun fromRaw(
            raw: RawMembership,
            body: ResolvedBody,
            person: ResolvedPerson,
            organization: ResolvedOrganization,
            onBehalfOf: ResolvedOrganization? = null,
        ): ResolvedMembership =
            ResolvedMembership(
                raw.resource,
                body,
                person,
                organization,
                onBehalfOf,
            ).apply { applyValuesFrom(raw) }
    }
}
