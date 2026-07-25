package nrw.openbert.backend.oparl.infrastructure.api.v1.mapper

import nrw.openbert.backend.oparl.domain.entity.AbstractAgendaItem
import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedAgendaItem
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlAgendaItemDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator.OparlUrlGenerator
import org.springframework.stereotype.Component

@Component
class OparlAgendaItemMapper(
    private val urlGenerator: OparlUrlGenerator,
    private val fileMapper: OparlFileMapper,
) {
    fun toDto(agendaItem: ResolvedAgendaItem): OparlAgendaItemDto {
        return OparlAgendaItemDto(
            id = urlGenerator.agendaItem(agendaItem.body.id, agendaItem.id),
            type = AbstractAgendaItem.TYPE,
            meeting = urlGenerator.meeting(agendaItem.body.id, agendaItem.meeting.id),
            number = agendaItem.number,
            order = requireNotNull(agendaItem.order) {
                "Resolved agenda item ${agendaItem.id} has no order"
            },
            name = agendaItem.name,
            public = agendaItem.public,
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
