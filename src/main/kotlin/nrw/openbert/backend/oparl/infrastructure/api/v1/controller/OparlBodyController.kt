package nrw.openbert.backend.oparl.infrastructure.api.v1.controller

import nrw.openbert.backend.oparl.domain.entity.AbstractBody
import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedBody
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlBodyDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.dto.OparlListDto
import nrw.openbert.backend.oparl.infrastructure.api.v1.urlGenerator.OparlUrlGenerator
import nrw.openbert.backend.oparl.infrastructure.persitence.repository.ResolvedBodyRepository
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
    @GetMapping("oparl/v1.1/body")
    fun list(): ResponseEntity<OparlListDto<OparlBodyDto>> {
        val bodies = resolvedBodyRepository.findAll().map { it.toDto() }

        return ResponseEntity.ok(
            OparlListDto(
                urlGenerator.bodyList(),
                bodies,
            )
        )
    }

    @GetMapping("oparl/v1.1/body/{id}")
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
            id = urlGenerator.body(id),
            type = AbstractBody.TYPE,
            name = name,
            organization = urlGenerator.organizationList(id),
            person = urlGenerator.personList(id),
            meeting = urlGenerator.meetingList(id),
            paper = urlGenerator.paperList(id),
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
            agendaItem = urlGenerator.agendaItemList(id),
            consultation = urlGenerator.consultationList(id),
            file = urlGenerator.fileList(id),
            locationList = urlGenerator.locationList(id),
            legislativeTermList = urlGenerator.legislativeTermList(id),
            membership = urlGenerator.membershipList(id),
            classification = classification,
            keyword = keyword.toList().takeIf { it.isNotEmpty() },
            created = created.toString(),
            modified = modified.toString(),
            web = web,
            deleted = deleted,
        )
    }
}