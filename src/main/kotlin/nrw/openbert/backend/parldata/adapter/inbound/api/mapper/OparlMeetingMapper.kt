package nrw.openbert.backend.parldata.adapter.inbound.api.mapper

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlMeetingDto
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.domain.entity.AbstractMeeting
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedMeeting
import org.springframework.stereotype.Component

@Component
class OparlMeetingMapper(
    private val urlGenerator: OparlUrlGenerator,
    private val agendaItemMapper: OparlAgendaItemMapper,
) {
    fun toDto(meeting: ResolvedMeeting): OparlMeetingDto =
        OparlMeetingDto(
            id = urlGenerator.meeting.get(meeting.body.id, meeting.id),
            type = meeting.getType().url,
            name = meeting.name,
            meetingState = meeting.meetingState,
            cancelled = meeting.cancelled,
            start = meeting.start?.toInstant()?.toString(),
            end = meeting.end?.toInstant()?.toString(),
            organization =
                meeting.organizations
                    .map { urlGenerator.organization.get(it.body.id, it.id) }
                    .takeIf { it.isNotEmpty() },
            agendaItem =
                meeting.agendaItems
                    .map(agendaItemMapper::toDto)
                    .takeIf { it.isNotEmpty() },
            license = meeting.license,
            keyword = meeting.keyword.toList().takeIf { it.isNotEmpty() },
            created = meeting.created.toString(),
            modified = meeting.modified.toString(),
            web = meeting.web,
            deleted = meeting.deleted,
        )
}
