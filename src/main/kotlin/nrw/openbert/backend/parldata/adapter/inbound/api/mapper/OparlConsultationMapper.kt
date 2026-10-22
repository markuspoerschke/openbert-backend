package nrw.openbert.backend.parldata.adapter.inbound.api.mapper

import nrw.openbert.backend.parldata.domain.entity.AbstractConsultation
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedConsultation
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlConsultationDto
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
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
            id = urlGenerator.consultation.get(consultation.body.id, consultation.id),
            type = consultation.getType().url,
            paper = consultation.paper
                .takeIf { includePaper }
                ?.let { urlGenerator.paper.get(it.body.id, it.id) },
            agendaItem = urlGenerator.agendaItem.get(
                consultation.agendaItem.body.id,
                consultation.agendaItem.id,
            ),
            meeting = urlGenerator.meeting.get(consultation.meeting.body.id, consultation.meeting.id),
            organization = consultation.organizations
                .map { urlGenerator.organization.get(it.body.id, it.id) }
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
