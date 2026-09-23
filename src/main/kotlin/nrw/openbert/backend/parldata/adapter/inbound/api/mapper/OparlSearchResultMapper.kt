package nrw.openbert.backend.parldata.adapter.inbound.api.mapper

import jakarta.persistence.EntityManager
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedAgendaItem
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedBody
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedConsultation
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedFile
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedLegislativeTerm
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedLocation
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedMeeting
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedMembership
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedOrganization
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedPaper
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedPerson
import nrw.openbert.backend.parldata.adapter.outbound.persistence.repository.OparlSearchEntityType
import nrw.openbert.backend.parldata.adapter.outbound.persistence.repository.OparlSearchReference
import org.springframework.stereotype.Component

@Component
class OparlSearchResultMapper(
    private val entityManager: EntityManager,
    private val bodyMapper: OparlBodyMapper,
    private val legislativeTermMapper: OparlLegislativeTermMapper,
    private val organizationMapper: OparlOrganizationMapper,
    private val personMapper: OparlPersonMapper,
    private val membershipMapper: OparlMembershipMapper,
    private val meetingMapper: OparlMeetingMapper,
    private val agendaItemMapper: OparlAgendaItemMapper,
    private val paperMapper: OparlPaperMapper,
    private val consultationMapper: OparlConsultationMapper,
    private val fileMapper: OparlFileMapper,
    private val locationMapper: OparlLocationMapper,
) {
    fun toDto(reference: OparlSearchReference): Any {
        return when (reference.entityType) {
            OparlSearchEntityType.BODY -> bodyMapper.toDto(find<ResolvedBody>(reference))
            OparlSearchEntityType.LEGISLATIVE_TERM -> {
                legislativeTermMapper.toDto(find<ResolvedLegislativeTerm>(reference))
            }
            OparlSearchEntityType.ORGANIZATION -> {
                organizationMapper.toDto(find<ResolvedOrganization>(reference))
            }
            OparlSearchEntityType.PERSON -> personMapper.toDto(find<ResolvedPerson>(reference))
            OparlSearchEntityType.MEMBERSHIP -> {
                membershipMapper.toDto(find<ResolvedMembership>(reference))
            }
            OparlSearchEntityType.MEETING -> meetingMapper.toDto(find<ResolvedMeeting>(reference))
            OparlSearchEntityType.AGENDA_ITEM -> {
                agendaItemMapper.toDto(find<ResolvedAgendaItem>(reference))
            }
            OparlSearchEntityType.PAPER -> paperMapper.toDto(find<ResolvedPaper>(reference))
            OparlSearchEntityType.CONSULTATION -> {
                consultationMapper.toDto(find<ResolvedConsultation>(reference))
            }
            OparlSearchEntityType.FILE -> fileMapper.toDto(find<ResolvedFile>(reference))
            OparlSearchEntityType.LOCATION -> locationMapper.toDto(find<ResolvedLocation>(reference))
        }
    }

    private inline fun <reified T : Any> find(reference: OparlSearchReference): T {
        return checkNotNull(entityManager.find(T::class.java, reference.entityId)) {
            "Search result ${reference.entityType}/${reference.entityId} no longer exists"
        }
    }
}
