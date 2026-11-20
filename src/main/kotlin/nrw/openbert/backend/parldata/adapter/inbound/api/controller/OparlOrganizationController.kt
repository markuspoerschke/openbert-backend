package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlConsultationDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlMeetingDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlOrganizationDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import nrw.openbert.backend.parldata.adapter.inbound.api.error.BadRequestProblem
import nrw.openbert.backend.parldata.adapter.inbound.api.error.NotFoundProblem
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlConsultationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlMeetingMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlOrganizationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.specification.toListSpecification
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedConsultationRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedMeetingRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedOrganizationRepository
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Tag(
    name = "Oparl Organization",
    externalDocs =
        ExternalDocumentation(
            "Oparl Specs",
            "https://oparl.org/spezifikation/online-ansicht/#entity-organization",
        ),
)
@RestController
class OparlOrganizationController(
    private val urlGenerator: OparlUrlGenerator,
    private val consultationMapper: OparlConsultationMapper,
    private val meetingMapper: OparlMeetingMapper,
    private val organizationMapper: OparlOrganizationMapper,
    private val listPaginationMapper: OparlListPaginationMapper,
    private val listLinksMapper: OparlListLinksMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedConsultationRepository: ResolvedConsultationRepository,
    private val resolvedMeetingRepository: ResolvedMeetingRepository,
    private val resolvedOrganizationRepository: ResolvedOrganizationRepository,
) {
    @BadRequestProblem
    @NotFoundProblem
    @GetMapping("oparl/v1.1/body/{bodyId}/organization")
    fun list(
        @PathVariable bodyId: UUID,
        @ParameterObject @Valid pagination: OparlPaginationRequestDto,
    ): ResponseEntity<OparlListDto<OparlOrganizationDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Body $bodyId does not exist.")
        }

        val organizations =
            resolvedOrganizationRepository
                .findAll(pagination.toListSpecification(bodyId = bodyId), pagination.toSpringPageRequest())
                .map(organizationMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.organization.list(bodyId, pagination),
                data = organizations.toList(),
                pagination = listPaginationMapper.toDto(organizations),
                links =
                    listLinksMapper.toDto(
                        organizations,
                        { page -> urlGenerator.organization.list(bodyId, pagination.copy(page = page)) },
                    ),
            ),
        )
    }

    @NotFoundProblem
    @GetMapping("oparl/v1.1/body/{bodyId}/organization/{organizationId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable organizationId: UUID,
    ): ResponseEntity<OparlOrganizationDto> {
        val organization =
            resolvedOrganizationRepository
                .findByIdAndBodyId(organizationId, bodyId)
                .orElseThrow {
                    ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Organization $organizationId does not exist in body $bodyId.",
                    )
                }

        return ResponseEntity.ok(organizationMapper.toDto(organization))
    }

    @BadRequestProblem
    @NotFoundProblem
    @GetMapping("oparl/v1.1/body/{bodyId}/organization/{organizationId}/meeting")
    fun listMeetings(
        @PathVariable bodyId: UUID,
        @PathVariable organizationId: UUID,
        @ParameterObject @Valid pagination: OparlPaginationRequestDto,
    ): ResponseEntity<OparlListDto<OparlMeetingDto>> {
        if (!resolvedOrganizationRepository.findByIdAndBodyId(organizationId, bodyId).isPresent) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Organization $organizationId does not exist in body $bodyId.",
            )
        }

        val meetings =
            resolvedMeetingRepository
                .findAll(
                    pagination.toListSpecification(bodyId = bodyId, organizationId = organizationId),
                    pagination.toSpringPageRequest(),
                ).map(meetingMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.organizationMeetingList(bodyId, organizationId, pagination),
                data = meetings.toList(),
                pagination = listPaginationMapper.toDto(meetings),
                links =
                    listLinksMapper.toDto(
                        meetings,
                        { page ->
                            urlGenerator.organizationMeetingList(bodyId, organizationId, pagination.copy(page = page))
                        },
                    ),
            ),
        )
    }

    @BadRequestProblem
    @NotFoundProblem
    @GetMapping("oparl/v1.1/body/{bodyId}/organization/{organizationId}/consultation")
    fun listConsultations(
        @PathVariable bodyId: UUID,
        @PathVariable organizationId: UUID,
        @ParameterObject @Valid pagination: OparlPaginationRequestDto,
    ): ResponseEntity<OparlListDto<OparlConsultationDto>> {
        if (!resolvedOrganizationRepository.findByIdAndBodyId(organizationId, bodyId).isPresent) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Organization $organizationId does not exist in body $bodyId.",
            )
        }

        val consultations =
            resolvedConsultationRepository
                .findAll(
                    pagination.toListSpecification(bodyId = bodyId, organizationId = organizationId),
                    pagination.toSpringPageRequest(),
                ).map(consultationMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id =
                    urlGenerator.organizationConsultationList(
                        bodyId,
                        organizationId,
                        pagination,
                    ),
                data = consultations.toList(),
                pagination = listPaginationMapper.toDto(consultations),
                links =
                    listLinksMapper.toDto(
                        consultations,
                        { page ->
                            urlGenerator.organizationConsultationList(
                                bodyId,
                                organizationId,
                                pagination.copy(page = page),
                            )
                        },
                    ),
            ),
        )
    }
}
