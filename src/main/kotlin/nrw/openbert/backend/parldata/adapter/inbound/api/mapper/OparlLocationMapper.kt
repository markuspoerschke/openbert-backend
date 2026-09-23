package nrw.openbert.backend.parldata.adapter.inbound.api.mapper

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlLocationDto
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.domain.entity.AbstractLocation
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedLocation
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
