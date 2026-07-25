package nrw.openbert.backend.oparl.infrastructure.api.v1.controller

import nrw.openbert.backend.oparl.domain.entity.AbstractOrganization
import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedOrganization
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlListDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlConsultationDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlMeetingDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlOrganizationDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.mapper.OparlConsultationMapper
import nrw.openbert.backend.oparl.infrastructure.api.v1.mapper.OparlMeetingMapper
import nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedBodyRepository
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedConsultationRepository
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedMeetingRepository
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedMembershipRepository
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedOrganizationRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.time.ZoneId
import java.util.Date
import java.util.UUID

@RestController
class OparlOrganizationController(
    private val urlGenerator: OparlUrlGenerator,
    private val consultationMapper: OparlConsultationMapper,
    private val meetingMapper: OparlMeetingMapper,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedConsultationRepository: ResolvedConsultationRepository,
    private val resolvedMeetingRepository: ResolvedMeetingRepository,
    private val resolvedMembershipRepository: ResolvedMembershipRepository,
    private val resolvedOrganizationRepository: ResolvedOrganizationRepository,
) {
    @GetMapping("oparl/v1.1/body/{bodyId}/organization")
    fun list(@PathVariable bodyId: UUID): ResponseEntity<OparlListDto<OparlOrganizationDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val organizations = resolvedOrganizationRepository
            .findAllByBody_IdOrderById(bodyId)
            .map { it.toDto() }

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

        return ResponseEntity.ok(organization.get().toDto())
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

    private fun ResolvedOrganization.toDto(): OparlOrganizationDto {
        return OparlOrganizationDto(
            id = urlGenerator.organization(body.id, id),
            type = AbstractOrganization.TYPE,
            body = urlGenerator.body(body.id),
            membership = resolvedMembershipRepository
                .findAllByOrganization_IdAndBody_IdOrderById(id, body.id)
                .map { urlGenerator.membership(body.id, it.id) }
                .takeIf { it.isNotEmpty() },
            meeting = urlGenerator.organizationMeetingList(body.id, id),
            consultation = urlGenerator.organizationConsultationList(body.id, id),
            name = name,
            shortName = shortName,
            post = post.toList().takeIf { it.isNotEmpty() },
            organizationType = organizationType,
            classification = classification,
            startDate = startDate?.toOparlDate(),
            endDate = endDate?.toOparlDate(),
            website = website?.value,
            license = license,
            keyword = keyword.toList().takeIf { it.isNotEmpty() },
            created = created.toString(),
            modified = modified.toString(),
            web = web,
            deleted = deleted,
        )
    }

    private fun Date.toOparlDate(): String {
        return toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString()
    }
}
