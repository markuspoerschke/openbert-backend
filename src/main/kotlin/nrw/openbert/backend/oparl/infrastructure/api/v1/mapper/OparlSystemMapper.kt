package nrw.openbert.backend.oparl.infrastructure.api.v1.mapper

import nrw.openbert.backend.oparl.domain.entity.AbstractSystem
import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedSystem
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlSystemDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator.OparlUrlGenerator
import org.springframework.stereotype.Component

@Component
class OparlSystemMapper(
    private val urlGenerator: OparlUrlGenerator,
) {
    fun toDto(system: ResolvedSystem): OparlSystemDto {
        return OparlSystemDto(
            id = urlGenerator.system(),
            type = AbstractSystem.TYPE,
            oparlVersion = system.oparlVersion,
            body = urlGenerator.bodyList(),
            otherOparlVersions = system.otherOparlVersions.toList().takeIf { it.isNotEmpty() },
            license = system.license?.value,
            name = system.name,
            contactEmail = system.contactEmail,
            contactName = system.contactName,
            website = system.website?.value,
            vendor = system.vendor?.value,
            product = system.product?.value,
            created = system.created.toString(),
            modified = system.modified.toString(),
            web = system.web,
            deleted = system.deleted,
        )
    }
}
