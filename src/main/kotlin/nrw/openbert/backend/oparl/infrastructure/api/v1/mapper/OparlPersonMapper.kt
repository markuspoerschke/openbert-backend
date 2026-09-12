package nrw.openbert.backend.oparl.infrastructure.api.v1.mapper

import nrw.openbert.backend.oparl.domain.entity.AbstractPerson
import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedPerson
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlPersonDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedMembershipRepository
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
            type = AbstractPerson.TYPE,
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
