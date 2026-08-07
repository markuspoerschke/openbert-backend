package nrw.openbert.backend.resolve.application

import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedOrganization
import nrw.openbert.backend.oparl.domain.repository.raw.RawBodyRepository
import nrw.openbert.backend.oparl.domain.repository.raw.RawOrganizationRepository
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedBodyRepository
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedOrganizationRepository
import nrw.openbert.backend.resolve.domain.entity.ResolveEntityType
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class OrganizationResolver(
    private val rawRepository: RawOrganizationRepository,
    private val rawBodyRepository: RawBodyRepository,
    private val resolvedRepository: ResolvedOrganizationRepository,
    private val resolvedBodyRepository: ResolvedBodyRepository,
) : EntityResolver {
    override val entityType = ResolveEntityType.ORGANIZATION

    override fun resolve(rawEntityId: UUID) {
        val raw = rawRepository.findById(rawEntityId).orElseThrow {
            IllegalArgumentException("Raw organization with id $rawEntityId does not exist")
        }
        val bodyUrl = raw.bodyUrl
            ?: throw UnresolvedRelationException("Missing body relation on ${raw.resource.url.value}")
        val bodyResource = OparlResource(raw.resource.system, bodyUrl)
        val body = resolvedBodyRepository.findByResource(bodyResource)
            ?: rawBodyRepository.findByResource(bodyResource)?.let { rawBody ->
                nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedBody.fromRaw(rawBody)
                    .let(resolvedBodyRepository::save)
            }
            ?: throw UnresolvedRelationException("Referenced body ${bodyUrl.value} has not been crawled")
        val resolved = resolvedRepository.findByResource(raw.resource)
            ?: ResolvedOrganization.fromRaw(raw, body)

        if (resolved.body.id != body.id) {
            throw UnresolvedRelationException("Conflicting body relation on ${raw.resource.url.value}")
        }
        resolved.applyValuesFrom(raw)
        resolvedRepository.save(resolved)
    }
}

class UnresolvedRelationException(message: String) : IllegalStateException(message)
