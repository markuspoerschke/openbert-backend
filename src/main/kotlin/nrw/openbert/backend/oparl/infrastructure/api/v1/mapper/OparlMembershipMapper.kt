package nrw.openbert.backend.oparl.infrastructure.api.v1.mapper

import nrw.openbert.backend.oparl.domain.entity.AbstractMembership
import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedMembership
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlMembershipDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator.OparlUrlGenerator
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
    ): OparlMembershipDto {
        return OparlMembershipDto(
            id = urlGenerator.membership(membership.body.id, membership.id),
            type = AbstractMembership.TYPE,
            person = membership.person
                .takeIf { includePerson }
                ?.let { urlGenerator.person(it.body.id, it.id) },
            organization = membership.organization
                .let { urlGenerator.organization(it.body.id, it.id) },
            role = membership.role,
            votingRight = membership.votingRight,
            startDate = membership.startDate?.toOparlDate(),
            endDate = membership.endDate?.toOparlDate(),
            onBehalfOf = membership.onBehalfOf
                ?.let { urlGenerator.organization(it.body.id, it.id) },
            license = membership.license,
            keyword = membership.keyword.toList().takeIf { it.isNotEmpty() },
            created = membership.created.toString(),
            modified = membership.modified.toString(),
            web = membership.web,
            deleted = membership.deleted,
        )
    }

    private fun Date.toOparlDate(): String {
        return toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString()
    }
}
