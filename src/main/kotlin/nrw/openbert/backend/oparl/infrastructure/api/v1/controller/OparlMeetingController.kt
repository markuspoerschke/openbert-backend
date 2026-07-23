package nrw.openbert.backend.oparl.infrastructure.api.v1.controller

import nrw.openbert.backend.oparl.domain.entity.AbstractMeeting
import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedMeeting
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlListDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlMeetingDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.oparl.infrastructure.persitence.repository.ResolvedBodyRepository
import nrw.openbert.backend.oparl.infrastructure.persitence.repository.ResolvedMeetingRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class OparlMeetingController(
    private val urlGenerator: OparlUrlGenerator,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedMeetingRepository: ResolvedMeetingRepository,
) {
    @GetMapping("oparl/v1.1/body/{bodyId}/meeting")
    fun list(@PathVariable bodyId: UUID): ResponseEntity<OparlListDto<OparlMeetingDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val meetings = resolvedMeetingRepository
            .findAllByBody_IdOrderById(bodyId)
            .map { it.toDto() }

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.meetingList(bodyId),
                data = meetings,
            )
        )
    }

    @GetMapping("oparl/v1.1/meeting/{id}")
    fun get(@PathVariable id: UUID): ResponseEntity<OparlMeetingDto> {
        val meeting = resolvedMeetingRepository.findById(id)

        if (!meeting.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(meeting.get().toDto())
    }

    private fun ResolvedMeeting.toDto(): OparlMeetingDto {
        return OparlMeetingDto(
            id = urlGenerator.meeting(id),
            type = AbstractMeeting.TYPE,
            name = name,
            meetingState = meetingState,
            cancelled = cancelled,
            start = start?.toInstant()?.toString(),
            end = end?.toInstant()?.toString(),
            license = license,
            keyword = keyword.toList().takeIf { it.isNotEmpty() },
            created = created.toString(),
            modified = modified.toString(),
            web = web,
            deleted = deleted,
        )
    }
}
