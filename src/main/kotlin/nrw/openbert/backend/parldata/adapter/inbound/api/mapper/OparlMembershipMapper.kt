package nrw.openbert.backend.parldata.adapter.inbound.api.mapper

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlMembershipDto
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.domain.entity.AbstractMembership
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedMembership
import org.springframework.stereotype.Component
import java.time.ZoneId
import java.util.Date

@Component
class OparlMembershipMapper(
    private val urlGenerator: OparlUrlGenerator,
) {
    fun toDto(
        membership: ResolvedMembership,
        includePerson: Boolean = true,
    ): OparlMembershipDto =
        OparlMembershipDto(
            id = urlGenerator.membership.get(membership.body.id, membership.id),
            type = membership.getType().url,
            person =
                membership.person
                    .takeIf { includePerson }
                    ?.let { urlGenerator.person.get(it.body.id, it.id) },
            organization =
                membership.organization
                    .let { urlGenerator.organization.get(it.body.id, it.id) },
            role = membership.role,
            votingRight = membership.votingRight,
            startDate = membership.startDate?.toOparlDate(),
            endDate = membership.endDate?.toOparlDate(),
            onBehalfOf =
                membership.onBehalfOf
                    ?.let { urlGenerator.organization.get(it.body.id, it.id) },
            license = membership.license,
            keyword = membership.keyword.toList().takeIf { it.isNotEmpty() },
            created = membership.created.toString(),
            modified = membership.modified.toString(),
            web = membership.web,
            deleted = membership.deleted,
        )

    private fun Date.toOparlDate(): String = toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString()
}
