package nrw.openbert.backend.parldata.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.parldata.domain.entity.AbstractLegislativeTerm
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawLegislativeTerm

@Entity(name = "oparl_resolved_legislativeTerm")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class ResolvedLegislativeTerm(
    resource: OparlResource,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "body_id", nullable = false, updatable = false)
    val body: ResolvedBody,
) : AbstractLegislativeTerm(resource) {
    companion object {
        fun fromRaw(
            raw: RawLegislativeTerm,
            body: ResolvedBody,
        ): ResolvedLegislativeTerm = ResolvedLegislativeTerm(raw.resource, body).apply { applyValuesFrom(raw) }
    }
}
