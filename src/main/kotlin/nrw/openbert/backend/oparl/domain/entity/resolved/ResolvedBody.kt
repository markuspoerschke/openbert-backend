package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.oparl.domain.entity.AbstractBody
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawBody

@Entity(name = "oparl_resolved_body")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class ResolvedBody(resource: OparlResource) : AbstractBody(resource) {
    @OneToMany(mappedBy = "body")
    val organizations: MutableList<ResolvedOrganization> = mutableListOf()

    @OneToMany(mappedBy = "body")
    val persons: MutableList<ResolvedPerson> = mutableListOf()

    companion object {
        fun fromRaw(raw: RawBody): ResolvedBody {
            return ResolvedBody(raw.resource).apply { applyValuesFrom(raw) }
        }
    }
}
