package nrw.openbert.backend.domain.oparl.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.domain.oparl.entity.AbstractFile
import nrw.openbert.backend.domain.oparl.entity.raw.RawFile
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId

@Entity(name = "oparl_resolved_file")
class ResolvedFile(id: OparlEntityId) : AbstractFile(id) {
    companion object {
        fun fromRaw(raw: RawFile): ResolvedFile {
            return ResolvedFile(raw.id).apply { applyValuesFrom(raw) }
        }
    }
}
