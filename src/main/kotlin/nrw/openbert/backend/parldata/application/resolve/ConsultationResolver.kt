package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.application.port.outbound.repository.RawConsultationRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedConsultationRepository
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedConsultation
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ConsultationResolver(
    private val rawRepository: RawConsultationRepository,
    private val resolvedRepository: ResolvedConsultationRepository,
    private val paperResolver: PaperResolver,
    private val agendaItemResolver: AgendaItemResolver,
    private val meetingResolver: MeetingResolver,
    private val organizationResolver: OrganizationResolver,
) : AbstractEntityResolver() {
    override val entityType = OparlType.CONSULTATION

    override fun resolve(rawEntityId: UUID) {
        val raw = rawRepository.findById(rawEntityId).orElseThrow {
            IllegalArgumentException("Raw consultation with id $rawEntityId does not exist")
        }
        val paper = paperResolver.resolve(relatedResource(raw.resource, required(raw.paperUrl, "paper", raw.resource)))
        val agendaItem = agendaItemResolver.resolve(
            relatedResource(raw.resource, required(raw.agendaItemUrl, "agenda item", raw.resource)),
        )
        val meeting = meetingResolver.resolve(
            relatedResource(raw.resource, required(raw.meetingUrl, "meeting", raw.resource)),
        )
        val organizations = raw.organizationUrls.map {
            organizationResolver.resolve(relatedResource(raw.resource, OparlUrl(it)))
        }
        val body = commonBody(
            raw.resource,
            listOf(paper.body, agendaItem.body, meeting.body) + organizations.map { it.body },
        )
        val resolved = resolvedRepository.findByResource(raw.resource)
            ?: ResolvedConsultation.fromRaw(raw, body, paper, agendaItem, meeting, organizations)

        requireRelation("body", raw.resource, resolved.body.id, body.id)
        requireRelation("paper", raw.resource, resolved.paper.id, paper.id)
        requireRelation("agenda item", raw.resource, resolved.agendaItem.id, agendaItem.id)
        requireRelation("meeting", raw.resource, resolved.meeting.id, meeting.id)
        resolved.applyValuesFrom(raw)
        resolved.applyRelationsFrom(organizations)
        resolvedRepository.save(resolved)
    }
}
