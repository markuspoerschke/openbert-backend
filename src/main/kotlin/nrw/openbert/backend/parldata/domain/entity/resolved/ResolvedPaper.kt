package nrw.openbert.backend.parldata.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.parldata.domain.entity.AbstractPaper
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawPaper

@Entity(name = "oparl_resolved_paper")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class ResolvedPaper(
    resource: OparlResource,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "body_id", nullable = false, updatable = false)
    val body: ResolvedBody,
) : AbstractPaper(resource) {
    @OneToMany(mappedBy = "paper")
    @OrderBy("id ASC")
    val consultations: MutableList<ResolvedConsultation> = mutableListOf()

    companion object {
        fun fromRaw(
            raw: RawPaper,
            body: ResolvedBody,
        ): ResolvedPaper = ResolvedPaper(raw.resource, body).apply { applyValuesFrom(raw) }
    }
}
