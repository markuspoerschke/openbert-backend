package nrw.openbert.backend.oparl.infrastructure.api.v1.mapper

import nrw.openbert.backend.oparl.domain.entity.AbstractMeeting
import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedMeeting
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlMeetingDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator.OparlUrlGenerator
import org.springframework.stereotype.Component

@Component
class OparlMeetingMapper(
    private val urlGenerator: OparlUrlGenerator,
) {
    fun toDto(meeting: ResolvedMeeting): OparlMeetingDto {
        return OparlMeetingDto(
            id = urlGenerator.meeting(meeting.body.id, meeting.id),
            type = AbstractMeeting.TYPE,
            name = meeting.name,
            meetingState = meeting.meetingState,
            cancelled = meeting.cancelled,
            start = meeting.start?.toInstant()?.toString(),
            end = meeting.end?.toInstant()?.toString(),
            license = meeting.license,
            keyword = meeting.keyword.toList().takeIf { it.isNotEmpty() },
            created = meeting.created.toString(),
            modified = meeting.modified.toString(),
            web = meeting.web,
            deleted = meeting.deleted,
        )
    }
}
