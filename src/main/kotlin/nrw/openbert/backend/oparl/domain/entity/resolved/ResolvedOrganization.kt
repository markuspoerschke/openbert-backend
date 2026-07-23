package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.oparl.domain.entity.AbstractOrganization
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawOrganization

@Entity(name = "oparl_resolved_organization")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class ResolvedOrganization(
    resource: OparlResource,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "body_id", nullable = false, updatable = false)
    val body: ResolvedBody,
) : AbstractOrganization(resource) {
    companion object {
        fun fromRaw(raw: RawOrganization, body: ResolvedBody): ResolvedOrganization {
            return ResolvedOrganization(raw.resource, body).apply { applyValuesFrom(raw) }
        }
    }
}
