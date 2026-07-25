package nrw.openbert.backend.oparl.infrastructure.api.v1.controller

import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlListDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlMeetingDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.mapper.OparlMeetingMapper
import nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedBodyRepository
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedMeetingRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class OparlMeetingController(
    private val urlGenerator: OparlUrlGenerator,
    private val meetingMapper: OparlMeetingMapper,
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
            .map(meetingMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.meetingList(bodyId),
                data = meetings,
            )
        )
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/meeting/{meetingId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable meetingId: UUID,
    ): ResponseEntity<OparlMeetingDto> {
        val meeting = resolvedMeetingRepository.findByIdAndBody_Id(meetingId, bodyId)

        if (!meeting.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(meetingMapper.toDto(meeting.get()))
    }
}
