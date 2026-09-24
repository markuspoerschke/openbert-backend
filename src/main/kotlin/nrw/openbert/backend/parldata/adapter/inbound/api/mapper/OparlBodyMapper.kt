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
    fun toDto(body: ResolvedBody): OparlBodyDto {
        return OparlBodyDto(
            id = urlGenerator.body(body.id),
            type = body.getType().url,
            name = body.name,
            organization = urlGenerator.organizationList(body.id),
            person = urlGenerator.personList(body.id),
            meeting = urlGenerator.meetingList(body.id),
            paper = urlGenerator.paperList(body.id),
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
            agendaItem = urlGenerator.agendaItemList(body.id),
            consultation = urlGenerator.consultationList(body.id),
            file = urlGenerator.fileList(body.id),
            locationList = urlGenerator.locationList(body.id),
            legislativeTermList = urlGenerator.legislativeTermList(body.id),
            membership = urlGenerator.membershipList(body.id),
            classification = body.classification,
            keyword = body.keyword.toList().takeIf { it.isNotEmpty() },
            created = body.created.toString(),
            modified = body.modified.toString(),
            web = body.web,
            deleted = body.deleted,
        )
    }
}
