package nrw.openbert.backend.parldata.adapter.inbound.api.mapper

import nrw.openbert.backend.parldata.adapter.inbound.api.dto.OparlPersonDto
import nrw.openbert.backend.parldata.adapter.inbound.api.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.parldata.domain.entity.AbstractPerson
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedPerson
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedMembershipRepository
import org.springframework.stereotype.Component

@Component
class OparlPersonMapper(
    private val urlGenerator: OparlUrlGenerator,
    private val membershipMapper: OparlMembershipMapper,
    private val resolvedMembershipRepository: ResolvedMembershipRepository,
) {
    fun toDto(person: ResolvedPerson): OparlPersonDto {
        return OparlPersonDto(
            id = urlGenerator.person(person.body.id, person.id),
            type = person.getType().url,
            body = urlGenerator.body(person.body.id),
            membership = resolvedMembershipRepository
                .findAllByPerson_IdAndBody_IdOrderById(person.id, person.body.id)
                .map { membershipMapper.toDto(it, includePerson = false) }
                .takeIf { it.isNotEmpty() },
            name = person.name,
            familyName = person.familyName,
            givenName = person.givenName,
            formOfAddress = person.formOfAddress,
            affix = person.affix,
            title = person.title.toList().takeIf { it.isNotEmpty() },
            gender = person.gender,
            phone = person.phone.toList().takeIf { it.isNotEmpty() },
            email = person.email.toList().takeIf { it.isNotEmpty() },
            status = person.status.toList().takeIf { it.isNotEmpty() },
            life = person.life,
            lifeSource = person.lifeSource,
            license = person.license,
            keyword = person.keyword.toList().takeIf { it.isNotEmpty() },
            created = person.created.toString(),
            modified = person.modified.toString(),
            web = person.web,
            deleted = person.deleted,
        )
    }
}
