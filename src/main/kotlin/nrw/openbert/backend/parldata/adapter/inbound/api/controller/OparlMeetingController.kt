package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlMeetingDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlMeetingMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedMeetingRepository
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Tag(
    name = "Oparl Meeting",
    externalDocs =
        ExternalDocumentation(
            "Oparl Specs",
            "https://oparl.org/spezifikation/online-ansicht/#entity-meeting",
        ),
)
@RestController
class OparlMeetingController(
    private val urlGenerator: OparlUrlGenerator,
    private val meetingMapper: OparlMeetingMapper,
    private val listPaginationMapper: OparlListPaginationMapper,
    private val listLinksMapper: OparlListLinksMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedMeetingRepository: ResolvedMeetingRepository,
) {
    @GetMapping("oparl/v1.1/body/{bodyId}/meeting")
    fun list(
        @PathVariable bodyId: UUID,
        @ParameterObject @Valid pagination: OparlPaginationRequestDto,
    ): ResponseEntity<OparlListDto<OparlMeetingDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val meetings =
            resolvedMeetingRepository
                .findAllByBodyIdOrderById(bodyId, pagination.toPageable())
                .map(meetingMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.meeting.list(bodyId, pagination.page),
                data = meetings.toList(),
                pagination = listPaginationMapper.toDto(meetings),
                links =
                    listLinksMapper.toDto(
                        meetings,
                        { page -> urlGenerator.meeting.list(bodyId, page) },
                    ),
            ),
        )
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/meeting/{meetingId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable meetingId: UUID,
    ): ResponseEntity<OparlMeetingDto> {
        val meeting = resolvedMeetingRepository.findByIdAndBodyId(meetingId, bodyId)

        if (!meeting.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(meetingMapper.toDto(meeting.get()))
    }
}
