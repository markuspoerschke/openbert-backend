package nrw.openbert.backend.oparl.infrastructure.api.v1.controller

import nrw.openbert.backend.oparl.domain.entity.AbstractPerson
import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedPerson
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlListDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlPersonDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedBodyRepository
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedPersonRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class OparlPersonController(
    private val urlGenerator: OparlUrlGenerator,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedPersonRepository: ResolvedPersonRepository,
) {
    @GetMapping("oparl/v1.1/body/{bodyId}/person")
    fun list(@PathVariable bodyId: UUID): ResponseEntity<OparlListDto<OparlPersonDto>> {
        if (!resolvedBodyRepository.existsById(bodyId)) {
            return ResponseEntity.notFound().build()
        }

        val persons = resolvedPersonRepository
            .findAllByBody_IdOrderById(bodyId)
            .map { it.toDto() }

        return ResponseEntity.ok(
            OparlListDto(
                id = urlGenerator.personList(bodyId),
                data = persons,
            )
        )
    }

    @GetMapping("oparl/v1.1/person/{id}")
    fun get(@PathVariable id: UUID): ResponseEntity<OparlPersonDto> {
        val person = resolvedPersonRepository.findById(id)

        if (!person.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(person.get().toDto())
    }

    private fun ResolvedPerson.toDto(): OparlPersonDto {
        return OparlPersonDto(
            id = urlGenerator.person(id),
            type = AbstractPerson.TYPE,
            body = urlGenerator.body(body.id),
            name = name,
            familyName = familyName,
            givenName = givenName,
            formOfAddress = formOfAddress,
            affix = affix,
            title = title.toList().takeIf { it.isNotEmpty() },
            gender = gender,
            phone = phone.toList().takeIf { it.isNotEmpty() },
            email = email.toList().takeIf { it.isNotEmpty() },
            status = status.toList().takeIf { it.isNotEmpty() },
            life = life,
            lifeSource = lifeSource,
            license = license,
            keyword = keyword.toList().takeIf { it.isNotEmpty() },
            created = created.toString(),
            modified = modified.toString(),
            web = web,
            deleted = deleted,
        )
    }
}
