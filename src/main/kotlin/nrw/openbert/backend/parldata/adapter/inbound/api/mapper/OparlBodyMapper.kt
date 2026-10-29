package nrw.openbert.backend.parldata.adapter.inbound.api.mapper

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlBodyDto
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.domain.entity.AbstractBody
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedBody
import org.springframework.stereotype.Component

@Component
class OparlBodyMapper(
    private val urlGenerator: OparlUrlGenerator,
    private val legislativeTermMapper: OparlLegislativeTermMapper,
) {
    fun toDto(body: ResolvedBody): OparlBodyDto =
        OparlBodyDto(
            id = urlGenerator.body(body.id),
            type = body.getType().url,
            name = body.name,
            organization = urlGenerator.organization.list(body.id),
            person = urlGenerator.person.list(body.id),
            meeting = urlGenerator.meeting.list(body.id),
            paper = urlGenerator.paper.list(body.id),
            legislativeTerm = body.legislativeTerms.map(legislativeTermMapper::toDto),
            shortName = body.shortName,
            website = body.website,
            license = body.license,
            licenseValidSince = body.licenseValidSince?.toInstant()?.toString(),
            oparlSince = body.oparlSince?.toInstant()?.toString(),
            ags = body.ags,
            rgs = body.rgs,
            equivalent = body.equivalent.toList().takeIf { it.isNotEmpty() },
            contactEmail = body.contactEmail,
            contactName = body.contactName,
            agendaItem = urlGenerator.agendaItem.list(body.id),
            consultation = urlGenerator.consultation.list(body.id),
            file = urlGenerator.file.list(body.id),
            locationList = urlGenerator.location.list(body.id),
            legislativeTermList = urlGenerator.legislativeTerm.list(body.id),
            membership = urlGenerator.membership.list(body.id),
            classification = body.classification,
            keyword = body.keyword.toList().takeIf { it.isNotEmpty() },
            created = body.created.toString(),
            modified = body.modified.toString(),
            web = body.web,
            deleted = body.deleted,
        )
}
