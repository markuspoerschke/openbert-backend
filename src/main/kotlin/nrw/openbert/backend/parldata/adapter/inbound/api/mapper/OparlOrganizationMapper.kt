package nrw.openbert.backend.parldata.adapter.inbound.api.mapper

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlOrganizationDto
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.domain.entity.AbstractOrganization
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedOrganization
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedMembershipRepository
import org.springframework.stereotype.Component
import java.time.ZoneId
import java.util.Date

@Component
class OparlOrganizationMapper(
    private val urlGenerator: OparlUrlGenerator,
    private val resolvedMembershipRepository: ResolvedMembershipRepository,
) {
    fun toDto(organization: ResolvedOrganization): OparlOrganizationDto {
        return OparlOrganizationDto(
            id = urlGenerator.organization(organization.body.id, organization.id),
            type = AbstractOrganization.TYPE,
            body = urlGenerator.body(organization.body.id),
            membership = resolvedMembershipRepository
                .findAllByOrganization_IdAndBody_IdOrderById(organization.id, organization.body.id)
                .map { urlGenerator.membership(organization.body.id, it.id) }
                .takeIf { it.isNotEmpty() },
            meeting = urlGenerator.organizationMeetingList(organization.body.id, organization.id),
            consultation = urlGenerator.organizationConsultationList(
                organization.body.id,
                organization.id,
            ),
            name = organization.name,
            shortName = organization.shortName,
            post = organization.post.toList().takeIf { it.isNotEmpty() },
            organizationType = organization.organizationType,
            classification = organization.classification,
            startDate = organization.startDate?.toOparlDate(),
            endDate = organization.endDate?.toOparlDate(),
            website = organization.website?.value,
            license = organization.license,
            keyword = organization.keyword.toList().takeIf { it.isNotEmpty() },
            created = organization.created.toString(),
            modified = organization.modified.toString(),
            web = organization.web,
            deleted = organization.deleted,
        )
    }

    private fun Date.toOparlDate(): String {
        return toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString()
    }
}
