package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.oparl.domain.entity.AbstractFile
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.raw.RawFile
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_resolved_file")
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["system_id", "url"])])
class ResolvedFile(resource: OparlResource) : AbstractFile(resource) {
    companion object {
        fun fromRaw(raw: RawFile): ResolvedFile {
            return ResolvedFile(raw.resource).apply { applyValuesFrom(raw) }
        }
    }
}
