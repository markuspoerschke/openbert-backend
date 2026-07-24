package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.oparl.domain.entity.AbstractFile
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawFile

@Entity(name = "oparl_resolved_file")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class ResolvedFile(
    resource: OparlResource,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "body_id", nullable = false, updatable = false)
    val body: ResolvedBody,
) : AbstractFile(resource) {
    companion object {
        fun fromRaw(raw: RawFile, body: ResolvedBody): ResolvedFile {
            return ResolvedFile(raw.resource, body).apply { applyValuesFrom(raw) }
        }
    }
}
