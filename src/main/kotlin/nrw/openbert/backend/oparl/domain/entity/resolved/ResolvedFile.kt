package nrw.openbert.backend.oparl.domain.entity.resolved

import jakarta.persistence.Entity
import nrw.openbert.backend.oparl.domain.entity.AbstractFile
import nrw.openbert.backend.oparl.domain.entity.raw.RawFile
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.UUID

@Entity(name = "oparl_resolved_file")
class ResolvedFile(systemId: UUID, url: OparlUrl) : AbstractFile(systemId, url) {
    companion object {
        fun fromRaw(raw: RawFile): ResolvedFile {
            return ResolvedFile(raw.systemId, raw.url).apply { applyValuesFrom(raw) }
        }
    }
}
