package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlConsultationDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlMeetingDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlOrganizationDto
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlConsultationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlMeetingMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.mapper.OparlOrganizationMapper
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedConsultationRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedMeetingRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedOrganizationRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class OparlOrganizationController(
    private val urlGenerator: OparlUrlGenerator,
    private val consultationMapper: OparlConsultationMapper,
    private val meetingMapper: OparlMeetingMapper,
    private val organizationMapper: OparlOrganizationMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedConsultationRepository: ResolvedConsultationRepository,
    private val resolvedMeetingRepository: ResolvedMeetingRepository,
    private val resolvedOrganizationRepository: ResolvedOrganizationRepository,
) {
    @GetMapping("oparl/v1.1/body/{bodyId}/organization")
    fun list(@PathVariable bodyId: UUID): ResponseEntity<OparlListDto<OparlOrganizationDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val organizations = resolvedOrganizationRepository
            .findAllByBody_IdOrderById(bodyId)
            .map(organizationMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.organizationList(bodyId),
                data = organizations,
            )
        )
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/organization/{organizationId}")
    fun get(
        @PathVariable bodyId: UUID,
        @PathVariable organizationId: UUID,
    ): ResponseEntity<OparlOrganizationDto> {
        val organization = resolvedOrganizationRepository.findByIdAndBody_Id(organizationId, bodyId)

        if (!organization.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(organizationMapper.toDto(organization.get()))
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/organization/{organizationId}/meeting")
    fun listMeetings(
        @PathVariable bodyId: UUID,
        @PathVariable organizationId: UUID,
    ): ResponseEntity<OparlListDto<OparlMeetingDto>> {
        if (!resolvedOrganizationRepository.findByIdAndBody_Id(organizationId, bodyId).isPresent) {
            return ResponseEntity.notFound().build()
        }

        val meetings = resolvedMeetingRepository
            .findAllDistinctByOrganizations_IdAndBody_IdOrderById(organizationId, bodyId)
            .map(meetingMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.organizationMeetingList(bodyId, organizationId),
                data = meetings,
            )
        )
    }

    @GetMapping("oparl/v1.1/body/{bodyId}/organization/{organizationId}/consultation")
    fun listConsultations(
        @PathVariable bodyId: UUID,
        @PathVariable organizationId: UUID,
    ): ResponseEntity<OparlListDto<OparlConsultationDto>> {
        if (!resolvedOrganizationRepository.findByIdAndBody_Id(organizationId, bodyId).isPresent) {
            return ResponseEntity.notFound().build()
        }

        val consultations = resolvedConsultationRepository
            .findAllDistinctByOrganizations_IdAndBody_IdOrderById(organizationId, bodyId)
            .map(consultationMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.organizationConsultationList(bodyId, organizationId),
                data = consultations,
            )
        )
    }
}
