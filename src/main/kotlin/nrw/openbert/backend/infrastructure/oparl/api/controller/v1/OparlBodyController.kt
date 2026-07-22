package nrw.openbert.backend.infrastructure.oparl.api.controller.v1

import nrw.openbert.backend.domain.oparl.entity.AbstractBody
import nrw.openbert.backend.domain.oparl.entity.resolved.ResolvedBody
import nrw.openbert.backend.infrastructure.oparl.api.OparlUrlGenerator
import nrw.openbert.backend.infrastructure.oparl.api.dto.v1.OparlBodyDto
import nrw.openbert.backend.infrastructure.oparl.api.dto.v1.OparlListDto
import nrw.openbert.backend.infrastructure.oparl.persitence.repository.ResolvedBodyRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class OparlBodyController(
    private val urlGenerator: OparlUrlGenerator,
    private val resolvedBodyRepository: ResolvedBodyRepository,
) {
    @GetMapping("oparl/1.1/bodies")
    fun list(): ResponseEntity<OparlListDto<OparlBodyDto>> {
        val bodies = resolvedBodyRepository.findAll().map { it.toDto() }

        return ResponseEntity.ok(
            OparlListDto(
                urlGenerator.bodiesUrl(),
                bodies,
            )
        )
    }

    @GetMapping("oparl/1.1/bodies/{id}")
    fun get(@PathVariable id: UUID): ResponseEntity<OparlBodyDto> {
        val resolvedBody = resolvedBodyRepository.findById(id)

        if (!resolvedBody.isPresent) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(resolvedBody.get().toDto())
    }

    private fun ResolvedBody.toDto(): OparlBodyDto {
        val bodyUrl = url.value.trimEnd('/')

        return OparlBodyDto(
            id = urlGenerator.bodyUrl(id),
            type = AbstractBody.TYPE,
            name = name,
            organization = urlGenerator.organizationsUrl(id),
            person = urlGenerator.personsUrl(id),
            meeting = urlGenerator.meetingsUrl(id),
            paper = urlGenerator.papersUrl(id),
            legislativeTerm = emptyList(),
            shortName = shortName,
            website = website?.value,
            license = license?.value,
            licenseValidSince = licenseValidSince?.toInstant()?.toString(),
            oparlSince = oparlSince?.toInstant()?.toString(),
            ags = ags,
            rgs = rgs,
            equivalent = equivalent.toList().takeIf { it.isNotEmpty() },
            contactEmail = contactEmail,
            contactName = contactName,
            agendaItem = urlGenerator.agendaItemsUrl(id),
            consultation = urlGenerator.consultationsUrl(id),
            file = urlGenerator.filesUrl(id),
            locationList = urlGenerator.locationsUrl(id),
            legislativeTermList = urlGenerator.legislativeTermsUrl(id),
            membership = urlGenerator.membershipsUrl(id),
            classification = classification,
            keyword = keyword.toList().takeIf { it.isNotEmpty() },
            created = created.toString(),
            modified = modified.toString(),
            web = web,
            deleted = deleted,
        )
    }
}