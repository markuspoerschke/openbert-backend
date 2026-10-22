package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.application.port.outbound.repository.RawAgendaItemRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawFileRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedAgendaItemRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedFileRepository
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedBody
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedFile
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class FileResolver(
    private val rawRepository: RawFileRepository,
    private val resolvedRepository: ResolvedFileRepository,
    private val rawAgendaItemRepository: RawAgendaItemRepository,
    private val resolvedAgendaItemRepository: ResolvedAgendaItemRepository,
    private val meetingResolver: MeetingResolver,
    private val paperResolver: PaperResolver,
) : AbstractEntityResolver() {
    override val entityType = OparlType.FILE

    override fun resolve(rawEntityId: UUID) {
        val raw = rawRepository.findById(rawEntityId).orElseThrow {
            IllegalArgumentException("Raw file with id $rawEntityId does not exist")
        }
        resolve(raw.resource)
    }

    fun resolve(resource: OparlResource, bodyHint: ResolvedBody? = null): ResolvedFile {
        val existing = resolvedRepository.findByResource(resource)
        val raw = rawRepository.findByResource(resource)
            ?: return existing ?: throw notCrawled("file", resource)
        val bodies = buildList {
            (bodyHint ?: existing?.body)?.let(::add)
            raw.meetingUrls.mapTo(this) { meetingResolver.resolve(relatedResource(raw.resource, OparlUrl(it))).body }
            raw.paperUrls.mapTo(this) { paperResolver.resolve(relatedResource(raw.resource, OparlUrl(it))).body }
            raw.agendaItemUrls.mapTo(this) { bodyForAgendaItem(relatedResource(raw.resource, OparlUrl(it))) }
        }
        val body = commonBody(raw.resource, bodies)
        val resolved = existing ?: ResolvedFile.fromRaw(raw, body)
        requireRelation("body", raw.resource, resolved.body.id, body.id)
        resolved.applyValuesFrom(raw)
        return resolvedRepository.save(resolved)
    }

    private fun bodyForAgendaItem(resource: OparlResource): ResolvedBody {
        // Only infer the body: resolving the agenda item itself would resolve this file again.
        resolvedAgendaItemRepository.findByResource(resource)?.let { return it.body }
        val raw = rawAgendaItemRepository.findByResource(resource) ?: throw notCrawled("agenda item", resource)
        return meetingResolver.resolve(
            relatedResource(raw.resource, required(raw.meetingUrl, "meeting", raw.resource)),
        ).body
    }
}
