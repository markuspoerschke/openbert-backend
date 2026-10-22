package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.application.port.outbound.repository.RawAgendaItemRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedAgendaItemRepository
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawAgendaItem
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedAgendaItem
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AgendaItemResolver(
    private val rawRepository: RawAgendaItemRepository,
    private val resolvedRepository: ResolvedAgendaItemRepository,
    private val meetingResolver: MeetingResolver,
    private val fileResolver: FileResolver,
) : AbstractEntityResolver() {
    override val entityType = OparlType.AGENDA_ITEM

    override fun resolve(rawEntityId: UUID) {
        val raw = rawRepository.findById(rawEntityId).orElseThrow {
            IllegalArgumentException("Raw agenda item with id $rawEntityId does not exist")
        }
        resolve(raw)
    }

    fun resolve(resource: OparlResource): ResolvedAgendaItem {
        val raw = rawRepository.findByResource(resource)
        if (raw != null) return resolve(raw)
        return resolvedRepository.findByResource(resource)
            ?: throw notCrawled("agenda item", resource)
    }

    private fun resolve(raw: RawAgendaItem): ResolvedAgendaItem {
        val meeting = meetingResolver.resolve(
            relatedResource(raw.resource, required(raw.meetingUrl, "meeting", raw.resource)),
        )
        val resolutionFile = raw.resolutionFileUrl?.let {
            fileResolver.resolve(relatedResource(raw.resource, it), meeting.body)
        }
        val auxiliaryFiles = raw.auxiliaryFileUrls.map {
            fileResolver.resolve(relatedResource(raw.resource, OparlUrl(it)), meeting.body)
        }
        val resolved = resolvedRepository.findByResource(raw.resource)
            ?: ResolvedAgendaItem.fromRaw(raw, meeting.body, meeting, resolutionFile, auxiliaryFiles)

        requireRelation("body", raw.resource, resolved.body.id, meeting.body.id)
        requireRelation("meeting", raw.resource, resolved.meeting.id, meeting.id)
        resolved.applyValuesFrom(raw)
        resolved.applyRelationsFrom(resolutionFile, auxiliaryFiles)
        return resolvedRepository.save(resolved)
    }
}
