package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.application.port.outbound.repository.RawLegislativeTermRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedLegislativeTermRepository
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedLegislativeTerm
import nrw.openbert.backend.parldata.domain.enums.OparlType
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class LegislativeTermResolver(
    private val rawRepository: RawLegislativeTermRepository,
    private val resolvedRepository: ResolvedLegislativeTermRepository,
    private val bodyResolver: BodyResolver,
) : AbstractEntityResolver() {
    override val entityType = OparlType.LEGISLATIVE_TERM

    override fun resolve(rawEntityId: UUID) {
        val raw =
            rawRepository.findById(rawEntityId).orElseThrow {
                IllegalArgumentException("Raw legislative term with id $rawEntityId does not exist")
            }
        val body = bodyResolver.resolve(relatedResource(raw.resource, required(raw.bodyUrl, "body", raw.resource)))
        val resolved =
            resolvedRepository.findByResource(raw.resource)
                ?: ResolvedLegislativeTerm.fromRaw(raw, body)

        requireRelation("body", raw.resource, resolved.body.id, body.id)
        resolved.applyValuesFrom(raw)
        resolvedRepository.save(resolved)
    }
}
