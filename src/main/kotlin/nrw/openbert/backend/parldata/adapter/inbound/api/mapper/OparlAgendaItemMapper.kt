package nrw.openbert.backend.parldata.adapter.inbound.api.mapper

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlAgendaItemDto
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedAgendaItem
import org.springframework.stereotype.Component

@Component
class OparlAgendaItemMapper(
    private val urlGenerator: OparlUrlGenerator,
    private val fileMapper: OparlFileMapper,
) {
    fun toDto(agendaItem: ResolvedAgendaItem): OparlAgendaItemDto {
        return OparlAgendaItemDto(
            id = urlGenerator.agendaItem.get(agendaItem.body.id, agendaItem.id),
            type = agendaItem.getType().url,
            meeting = urlGenerator.meeting.get(agendaItem.body.id, agendaItem.meeting.id),
            number = agendaItem.number,
            order = requireNotNull(agendaItem.order) {
                "Resolved agenda item ${agendaItem.id} has no order"
            },
            name = agendaItem.name,
            public = agendaItem.public,
            consultation = agendaItem.consultation
                ?.let { urlGenerator.consultation.get(it.body.id, it.id) },
            result = agendaItem.result,
            resolutionText = agendaItem.resolutionText,
            resolutionFile = agendaItem.resolutionFile?.let(fileMapper::toDto),
            auxiliaryFile = agendaItem.auxiliaryFiles
                .map(fileMapper::toDto)
                .takeIf { it.isNotEmpty() },
            start = agendaItem.start?.toInstant()?.toString(),
            end = agendaItem.end?.toInstant()?.toString(),
            license = agendaItem.license,
            keyword = agendaItem.keyword.toList().takeIf { it.isNotEmpty() },
            created = agendaItem.created.toString(),
            modified = agendaItem.modified.toString(),
            web = agendaItem.web,
            deleted = agendaItem.deleted,
        )
    }
}
