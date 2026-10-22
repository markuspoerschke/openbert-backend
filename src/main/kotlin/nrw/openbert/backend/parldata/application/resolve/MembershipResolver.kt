package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.application.port.outbound.repository.RawMembershipRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedMembershipRepository
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedMembership
import nrw.openbert.backend.parldata.domain.enums.OparlType
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MembershipResolver(
    private val rawRepository: RawMembershipRepository,
    private val resolvedRepository: ResolvedMembershipRepository,
    private val personResolver: PersonResolver,
    private val organizationResolver: OrganizationResolver,
) : AbstractEntityResolver() {
    override val entityType = OparlType.MEMBERSHIP

    override fun resolve(rawEntityId: UUID) {
        val raw = rawRepository.findById(rawEntityId).orElseThrow {
            IllegalArgumentException("Raw membership with id $rawEntityId does not exist")
        }
        val person = personResolver.resolve(relatedResource(raw.resource, required(raw.personUrl, "person", raw.resource)))
        val organization = organizationResolver.resolve(relatedResource(raw.resource, required(raw.organizationUrl, "organization", raw.resource)))
        val onBehalfOf = raw.onBehalfOfUrl?.let { organizationResolver.resolve(relatedResource(raw.resource, it)) }
        val body = commonBody(raw.resource, listOfNotNull(person.body, organization.body, onBehalfOf?.body))
        val resolved = resolvedRepository.findByResource(raw.resource)
            ?: ResolvedMembership.fromRaw(raw, body, person, organization, onBehalfOf)

        requireRelation("body", raw.resource, resolved.body.id, body.id)
        requireRelation("person", raw.resource, resolved.person.id, person.id)
        requireRelation("organization", raw.resource, resolved.organization.id, organization.id)
        resolved.applyValuesFrom(raw)
        resolved.applyRelationsFrom(onBehalfOf)
        resolvedRepository.save(resolved)
    }
}
