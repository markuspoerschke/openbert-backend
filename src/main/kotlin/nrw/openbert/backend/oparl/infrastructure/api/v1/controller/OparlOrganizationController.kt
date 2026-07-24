package nrw.openbert.backend.oparl.infrastructure.api.v1.controller

import nrw.openbert.backend.oparl.domain.entity.AbstractOrganization
import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedOrganization
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlListDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlOrganizationDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedBodyRepository
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
    private val resolvedBodyRepository: ResolvedBodyRepository,
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

    private fun ResolvedOrganization.toDto(): OparlOrganizationDto {
        return OparlOrganizationDto(
            id = urlGenerator.organization(body.id, id),
            type = AbstractOrganization.TYPE,
            body = urlGenerator.body(body.id),
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
