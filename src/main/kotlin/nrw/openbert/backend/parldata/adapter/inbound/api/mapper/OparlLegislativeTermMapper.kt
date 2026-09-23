package nrw.openbert.backend.parldata.adapter.inbound.api.mapper

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlLegislativeTermDto
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.domain.entity.AbstractLegislativeTerm
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedLegislativeTerm
import org.springframework.stereotype.Component
import java.time.ZoneId
import java.util.Date

@Component
class OparlLegislativeTermMapper(
    private val urlGenerator: OparlUrlGenerator,
) {
    fun toDto(legislativeTerm: ResolvedLegislativeTerm): OparlLegislativeTermDto {
        return OparlLegislativeTermDto(
            id = urlGenerator.legislativeTerm(legislativeTerm.body.id, legislativeTerm.id),
            type = AbstractLegislativeTerm.TYPE,
            body = urlGenerator.body(legislativeTerm.body.id),
            name = legislativeTerm.name,
            startDate = legislativeTerm.startDate?.toOparlDate(),
            endDate = legislativeTerm.endDate?.toOparlDate(),
            license = legislativeTerm.license,
            keyword = legislativeTerm.keyword.toList().takeIf { it.isNotEmpty() },
            created = legislativeTerm.created.toString(),
            modified = legislativeTerm.modified.toString(),
            web = legislativeTerm.web,
            deleted = legislativeTerm.deleted,
        )
    }

    private fun Date.toOparlDate(): String {
        return toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString()
    }
}
