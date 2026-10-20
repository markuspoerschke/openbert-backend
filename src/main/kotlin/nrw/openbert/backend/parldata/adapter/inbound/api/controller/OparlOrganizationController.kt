package nrw.openbert.backend.parldata.adapter.inbound.api.controller

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlConsultationDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlListDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlMeetingDto
import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlOrganizationDto
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
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

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
        @RequestParam page: Int = 1,
    ): ResponseEntity<OparlListDto<OparlOrganizationDto>> {
        if (page < 1) {
            return ResponseEntity.badRequest().build()
        }

        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val organizations = resolvedOrganizationRepository
            // please note that we need to subtract 1 from the page number because oparl expects a 1-based page number
            .findAllByBody_IdOrderById(bodyId, PageRequest.of(page - 1, PAGE_SIZE))
            .map(organizationMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.organizationList(bodyId, page),
                data = organizations.toList(),
                pagination = listPaginationMapper.toDto(organizations),
                links = listLinksMapper.toDto(
                    organizations,
                    { page -> urlGenerator.organizationList(bodyId, page) }
                ),
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
        @RequestParam page: Int = 1,
    ): ResponseEntity<OparlListDto<OparlMeetingDto>> {
        if (page < 1) {
            return ResponseEntity.badRequest().build()
        }

        if (!resolvedOrganizationRepository.findByIdAndBody_Id(organizationId, bodyId).isPresent) {
            return ResponseEntity.notFound().build()
        }

        val meetings = resolvedMeetingRepository
            // please note that we need to subtract 1 from the page number because oparl expects a 1-based page number
            .findAllDistinctByOrganizations_IdAndBody_IdOrderById(
                organizationId,
                bodyId,
                PageRequest.of(page - 1, PAGE_SIZE),
            )
            .map(meetingMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.organizationMeetingList(bodyId, organizationId, page),
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
        @RequestParam page: Int = 1,
    ): ResponseEntity<OparlListDto<OparlConsultationDto>> {
        if (page < 1) {
            return ResponseEntity.badRequest().build()
        }

        if (!resolvedOrganizationRepository.findByIdAndBody_Id(organizationId, bodyId).isPresent) {
            return ResponseEntity.notFound().build()
        }

        val consultations = resolvedConsultationRepository
            // please note that we need to subtract 1 from the page number because oparl expects a 1-based page number
            .findAllDistinctByOrganizations_IdAndBody_IdOrderById(
                organizationId,
                bodyId,
                PageRequest.of(page - 1, PAGE_SIZE),
            )
            .map(consultationMapper::toDto)

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.organizationConsultationList(bodyId, organizationId, page),
                data = consultations.toList(),
                pagination = listPaginationMapper.toDto(consultations),
                links = listLinksMapper.toDto(
                    consultations,
                    { page -> urlGenerator.organizationConsultationList(bodyId, organizationId, page) }
                ),
            )
        )
    }

    companion object {
        private const val PAGE_SIZE = 25
    }
}
