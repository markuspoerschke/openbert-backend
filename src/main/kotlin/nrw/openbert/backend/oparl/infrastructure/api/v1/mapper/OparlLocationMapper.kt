package nrw.openbert.backend.oparl.infrastructure.api.v1.mapper

import nrw.openbert.backend.oparl.domain.entity.AbstractLocation
import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedLocation
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlLocationDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator.OparlUrlGenerator
import org.springframework.stereotype.Component

@Component
class OparlLocationMapper(
    private val urlGenerator: OparlUrlGenerator,
) {
    fun toDto(location: ResolvedLocation): OparlLocationDto {
        return OparlLocationDto(
            id = urlGenerator.location(location.body.id, location.id),
            type = AbstractLocation.TYPE,
            description = location.description,
            geojson = location.geojson,
            streetAddress = location.streetAddress,
            room = location.room,
            postalCode = location.postalCode,
            subLocality = location.subLocality,
            locality = location.locality,
            bodies = listOf(urlGenerator.body(location.body.id)),
            license = location.license,
            keyword = location.keyword.toList().takeIf { it.isNotEmpty() },
            created = location.created.toString(),
            modified = location.modified.toString(),
            web = location.web,
            deleted = location.deleted,
        )
    }
}
