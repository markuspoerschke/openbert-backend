package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlConsultationDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlMeetingDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlOrganizationDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPaginationRequestDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlConsultationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListLinksMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlListPaginationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlMeetingMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlOrganizationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedConsultationRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedMeetingRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedOrganizationRepository
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Tag(name = "Oparl Organization", externalDocs = ExternalDocumentation("Oparl Specs", "https://oparl.org/spezifikation/online-ansicht/#entity-organization"))
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
    @GetMapping("oparl/v1.1/body/{bodyId}/organization")
    fun list(
        @PathVariable bodyId: UUID,
        @ParameterObject @Valid pagination: OparlPaginationRequestDto,
    ): ResponseEntity<OparlListDto<OparlOrganizationDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val organizations = resolvedOrganizationRepository
            .findAllByBodyIdOrderById(bodyId, pagination.toPageable())
            .map(organizationMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.organization.list(bodyId, pagination.page),
                data = organizations.toList(),
                pagination = listPaginationMapper.toDto(organizations),
                links = listLinksMapper.toDto(
                    organizations,
                    { page -> urlGenerator.organization.list(bodyId, page) }
                ),
            )
        )
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/organization/{organizationId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable organizationId: UUID,
    ): ResponseEntity<OparlOrganizationDto> {
        val organization = resolvedOrganizationRepository.findByIdAndBodyId(organizationId, bodyId)

        if (!organization.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(organizationMapper.toDto(organization.get()))
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/organization/{organizationId}/meeting")
    fun listMeetings(
        @PathVariable bodyId: UUID,
        @PathVariable organizationId: UUID,
        @ParameterObject @Valid pagination: OparlPaginationRequestDto,
    ): ResponseEntity<OparlListDto<OparlMeetingDto>> {
        if (!resolvedOrganizationRepository.findByIdAndBodyId(organizationId, bodyId).isPresent) {
            return ResponseEntity.notFound().build()
        }

        val meetings = resolvedMeetingRepository
            .findAllDistinctByOrganizationsIdAndBodyIdOrderById(
                organizationId,
                bodyId,
                pagination.toPageable(),
            )
            .map(meetingMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.organizationMeetingList(bodyId, organizationId, pagination.page),
                data = meetings.toList(),
                pagination = listPaginationMapper.toDto(meetings),
                links = listLinksMapper.toDto(
                    meetings,
                    { page -> urlGenerator.organizationMeetingList(bodyId, organizationId, page) }
                ),
            )
        )
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/organization/{organizationId}/consultation")
    fun listConsultations(
        @PathVariable bodyId: UUID,
        @PathVariable organizationId: UUID,
        @ParameterObject @Valid pagination: OparlPaginationRequestDto,
    ): ResponseEntity<OparlListDto<OparlConsultationDto>> {
        if (!resolvedOrganizationRepository.findByIdAndBodyId(organizationId, bodyId).isPresent) {
            return ResponseEntity.notFound().build()
        }

        val consultations = resolvedConsultationRepository
            .findAllDistinctByOrganizationsIdAndBodyIdOrderById(
                organizationId,
                bodyId,
                pagination.toPageable(),
            )
            .map(consultationMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.organizationConsultationList(bodyId, organizationId, pagination.page),
                data = consultations.toList(),
                pagination = listPaginationMapper.toDto(consultations),
                links = listLinksMapper.toDto(
                    consultations,
                    { page -> urlGenerator.organizationConsultationList(bodyId, organizationId, page) }
                ),
            )
        )
    }
}
