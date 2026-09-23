package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedSystem
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawSystemRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedSystemRepository
import nrw.openbert.backend.parldata.domain.enums.ResolveEntityType
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SystemResolver(
    private val rawRepository: RawSystemRepository,
    private val resolvedRepository: ResolvedSystemRepository,
) : EntityResolver {
    override val entityType = ResolveEntityType.SYSTEM

    override fun resolve(rawEntityId: UUID) {
        val raw = rawRepository.findById(rawEntityId).orElseThrow {
            IllegalArgumentException("Raw system with id $rawEntityId does not exist")
        }
        val resolved = resolvedRepository.findByResource(raw.resource)
            ?: ResolvedSystem.fromRaw(raw)

        resolved.applyValuesFrom(raw)
        resolvedRepository.save(resolved)
    }
}
