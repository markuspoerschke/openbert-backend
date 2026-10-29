package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.application.port.outbound.repository.RawPersonRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedPersonRepository
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedPerson
import nrw.openbert.backend.parldata.domain.enums.OparlType
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PersonResolver(
    private val rawRepository: RawPersonRepository,
    private val resolvedRepository: ResolvedPersonRepository,
    private val bodyResolver: BodyResolver,
) : AbstractEntityResolver() {
    override val entityType = OparlType.PERSON

    override fun resolve(rawEntityId: UUID) {
        val raw =
            rawRepository.findById(rawEntityId).orElseThrow {
                IllegalArgumentException("Raw person with id $rawEntityId does not exist")
            }
        resolve(raw.resource)
    }

    fun resolve(resource: OparlResource): ResolvedPerson {
        val raw =
            rawRepository.findByResource(resource)
                ?: return resolvedRepository.findByResource(resource) ?: throw notCrawled("person", resource)
        val body = bodyResolver.resolve(relatedResource(raw.resource, required(raw.bodyUrl, "body", raw.resource)))
        val resolved = resolvedRepository.findByResource(resource) ?: ResolvedPerson.fromRaw(raw, body)

        requireRelation("body", raw.resource, resolved.body.id, body.id)
        resolved.applyValuesFrom(raw)
        return resolvedRepository.save(resolved)
    }
}
