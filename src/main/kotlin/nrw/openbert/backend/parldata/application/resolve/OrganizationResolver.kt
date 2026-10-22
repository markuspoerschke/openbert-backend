package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.application.port.outbound.repository.RawOrganizationRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedOrganizationRepository
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawOrganization
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedBody
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedOrganization
import nrw.openbert.backend.parldata.domain.enums.OparlType
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class OrganizationResolver(
    private val rawRepository: RawOrganizationRepository,
    private val resolvedRepository: ResolvedOrganizationRepository,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val bodyResolver: BodyResolver,
) : AbstractEntityResolver() {
    override val entityType = OparlType.ORGANIZATION

    override fun resolve(rawEntityId: UUID) {
        val raw = rawRepository.findById(rawEntityId).orElseThrow {
            IllegalArgumentException("Raw organization with id $rawEntityId does not exist")
        }
        val bodyResource = relatedResource(raw.resource, required(raw.bodyUrl, "body", raw.resource))
        val body = resolvedBodyRepository.findByResource(bodyResource) ?: bodyResolver.resolve(bodyResource)
        resolve(raw, body)
    }

    fun resolve(resource: OparlResource): ResolvedOrganization {
        val raw = rawRepository.findByResource(resource)
            ?: return resolvedRepository.findByResource(resource) ?: throw notCrawled("organization", resource)
        val body = bodyResolver.resolve(relatedResource(raw.resource, required(raw.bodyUrl, "body", raw.resource)))
        return resolve(raw, body)
    }

    private fun resolve(raw: RawOrganization, body: ResolvedBody): ResolvedOrganization {
        val resolved = resolvedRepository.findByResource(raw.resource)
            ?: ResolvedOrganization.fromRaw(raw, body)

        requireRelation("body", raw.resource, resolved.body.id, body.id)
        resolved.applyValuesFrom(raw)
        return resolvedRepository.save(resolved)
    }
}
