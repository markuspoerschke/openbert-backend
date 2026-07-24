package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.oparl.domain.entity.AbstractLegislativeTerm
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawLegislativeTerm

@Entity(name = "oparl_resolved_legislativeTerm")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class ResolvedLegislativeTerm(
    resource: OparlResource,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "body_id", nullable = false, updatable = false)
    val body: ResolvedBody,
) : AbstractLegislativeTerm(resource) {
    companion object {
        fun fromRaw(raw: RawLegislativeTerm, body: ResolvedBody): ResolvedLegislativeTerm {
            return ResolvedLegislativeTerm(raw.resource, body).apply { applyValuesFrom(raw) }
        }
    }
}
