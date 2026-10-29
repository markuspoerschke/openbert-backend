package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.application.port.outbound.repository.RawPaperRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedPaperRepository
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedPaper
import nrw.openbert.backend.parldata.domain.enums.OparlType
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PaperResolver(
    private val rawRepository: RawPaperRepository,
    private val resolvedRepository: ResolvedPaperRepository,
    private val bodyResolver: BodyResolver,
) : AbstractEntityResolver() {
    override val entityType = OparlType.PAPER

    override fun resolve(rawEntityId: UUID) {
        val raw =
            rawRepository.findById(rawEntityId).orElseThrow {
                IllegalArgumentException("Raw paper with id $rawEntityId does not exist")
            }
        resolve(raw.resource)
    }

    fun resolve(resource: OparlResource): ResolvedPaper {
        val raw =
            rawRepository.findByResource(resource)
                ?: return resolvedRepository.findByResource(resource) ?: throw notCrawled("paper", resource)
        val body = bodyResolver.resolve(relatedResource(raw.resource, required(raw.bodyUrl, "body", raw.resource)))
        val resolved = resolvedRepository.findByResource(resource) ?: ResolvedPaper.fromRaw(raw, body)

        requireRelation("body", raw.resource, resolved.body.id, body.id)
        resolved.applyValuesFrom(raw)
        return resolvedRepository.save(resolved)
    }
}
