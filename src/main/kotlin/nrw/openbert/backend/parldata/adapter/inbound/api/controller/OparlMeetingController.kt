package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlMeetingDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import nrw.openbert.backend.parldata.adapter.inbound.api.error.BadRequestProblem
import nrw.openbert.backend.parldata.adapter.inbound.api.error.NotFoundProblem
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlMeetingMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedMeetingRepository
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
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
    @BadRequestProblem
    @NotFoundProblem
    @GetMapping("oparl/v1.1/body/{bodyId}/meeting")
    fun list(
        @PathVariable bodyId: UUID,
        @ParameterObject @Valid pagination: OparlPaginationRequestDto,
    ): ResponseEntity<OparlListDto<OparlMeetingDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Body $bodyId does not exist.")
        }

        val meetings =
            resolvedMeetingRepository
                .findAllByBodyIdOrderById(bodyId, pagination.toSpringPageRequest())
                .map(meetingMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.meeting.list(bodyId, pagination.page, pagination.size),
                data = meetings.toList(),
                pagination = listPaginationMapper.toDto(meetings),
                links =
                    listLinksMapper.toDto(
                        meetings,
                        { page -> urlGenerator.meeting.list(bodyId, page, pagination.size) },
                    ),
            ),
        )
    }

    @NotFoundProblem
    @GetMapping("oparl/v1.1/body/{bodyId}/meeting/{meetingId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable meetingId: UUID,
    ): ResponseEntity<OparlMeetingDto> {
        val meeting =
            resolvedMeetingRepository
                .findByIdAndBodyId(meetingId, bodyId)
                .orElseThrow {
                    ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Meeting $meetingId does not exist in body $bodyId.",
                    )
                }

        return ResponseEntity.ok(meetingMapper.toDto(meeting))
    }
}
