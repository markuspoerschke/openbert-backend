package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.application.port.outbound.repository.RawBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawBody
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedBody
import nrw.openbert.backend.parldata.domain.enums.OparlType
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BodyResolver(
    private val rawRepository: RawBodyRepository,
    private val resolvedRepository: ResolvedBodyRepository,
) : AbstractEntityResolver() {
    override val entityType = OparlType.BODY

    override fun resolve(rawEntityId: UUID) {
        val raw =
            rawRepository.findById(rawEntityId).orElseThrow {
                IllegalArgumentException("Raw body with id $rawEntityId does not exist")
            }
        resolve(raw)
    }

    fun resolve(resource: OparlResource): ResolvedBody {
        val raw =
            rawRepository.findByResource(resource)
                ?: return resolvedRepository.findByResource(resource) ?: throw notCrawled("body", resource)
        return resolve(raw)
    }

    private fun resolve(raw: RawBody): ResolvedBody {
        val resolved = resolvedRepository.findByResource(raw.resource) ?: ResolvedBody.fromRaw(raw)
        resolved.applyValuesFrom(raw)
        return resolvedRepository.save(resolved)
    }
}
