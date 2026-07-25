package nrw.openbert.backend.oparl.infrastructure.api.v1.mapper

import nrw.openbert.backend.oparl.domain.entity.AbstractConsultation
import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedConsultation
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlConsultationDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator.OparlUrlGenerator
import org.springframework.stereotype.Component

@Component
class OparlConsultationMapper(
    private val urlGenerator: OparlUrlGenerator,
) {
    fun toDto(
        consultation: ResolvedConsultation,
        includePaper: Boolean = true,
    ): OparlConsultationDto {
        return OparlConsultationDto(
            id = urlGenerator.consultation(consultation.body.id, consultation.id),
            type = AbstractConsultation.TYPE,
            paper = consultation.paper
                .takeIf { includePaper }
                ?.let { urlGenerator.paper(it.body.id, it.id) },
            agendaItem = urlGenerator.agendaItem(
                consultation.agendaItem.body.id,
                consultation.agendaItem.id,
            ),
            meeting = urlGenerator.meeting(consultation.meeting.body.id, consultation.meeting.id),
            organization = consultation.organizations
                .map { urlGenerator.organization(it.body.id, it.id) }
                .takeIf { it.isNotEmpty() },
            authoritative = consultation.authoritative,
            role = consultation.role,
            license = consultation.license,
            keyword = consultation.keyword.toList().takeIf { it.isNotEmpty() },
            created = consultation.created.toString(),
            modified = consultation.modified.toString(),
            web = consultation.web,
            deleted = consultation.deleted,
        )
    }
}
