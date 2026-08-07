package nrw.openbert.backend.resolve.application

import nrw.openbert.backend.oparl.domain.entity.resolved.ResolvedBody
import nrw.openbert.backend.oparl.domain.repository.raw.RawBodyRepository
import nrw.openbert.backend.oparl.infrastructure.persistence.repository.ResolvedBodyRepository
import nrw.openbert.backend.resolve.domain.entity.ResolveEntityType
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BodyResolver(
    private val rawRepository: RawBodyRepository,
    private val resolvedRepository: ResolvedBodyRepository,
) : EntityResolver {
    override val entityType = ResolveEntityType.BODY

    override fun resolve(rawEntityId: UUID) {
        val raw = rawRepository.findById(rawEntityId).orElseThrow {
            IllegalArgumentException("Raw body with id $rawEntityId does not exist")
        }
        val resolved = resolvedRepository.findByResource(raw.resource)
            ?: ResolvedBody.fromRaw(raw)

        resolved.applyValuesFrom(raw)
        resolvedRepository.save(resolved)
    }
}
