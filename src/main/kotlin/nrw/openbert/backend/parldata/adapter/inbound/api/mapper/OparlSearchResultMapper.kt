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
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.domain.valueObject.OparlSearchReference
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
    fun toDto(reference: OparlSearchReference): Any =
        when (reference.entityType) {
            OparlType.BODY -> {
                bodyMapper.toDto(find<ResolvedBody>(reference))
            }

            OparlType.LEGISLATIVE_TERM -> {
                legislativeTermMapper.toDto(find<ResolvedLegislativeTerm>(reference))
            }

            OparlType.ORGANIZATION -> {
                organizationMapper.toDto(find<ResolvedOrganization>(reference))
            }

            OparlType.PERSON -> {
                personMapper.toDto(find<ResolvedPerson>(reference))
            }

            OparlType.MEMBERSHIP -> {
                membershipMapper.toDto(find<ResolvedMembership>(reference))
            }

            OparlType.MEETING -> {
                meetingMapper.toDto(find<ResolvedMeeting>(reference))
            }

            OparlType.AGENDA_ITEM -> {
                agendaItemMapper.toDto(find<ResolvedAgendaItem>(reference))
            }

            OparlType.PAPER -> {
                paperMapper.toDto(find<ResolvedPaper>(reference))
            }

            OparlType.CONSULTATION -> {
                consultationMapper.toDto(find<ResolvedConsultation>(reference))
            }

            OparlType.FILE -> {
                fileMapper.toDto(find<ResolvedFile>(reference))
            }

            OparlType.LOCATION -> {
                locationMapper.toDto(find<ResolvedLocation>(reference))
            }

            OparlType.SYSTEM -> {
                NotImplementedError("Mapping of System is unimplemented.")
            }
        }

    private inline fun <reified T : Any> find(reference: OparlSearchReference): T =
        checkNotNull(entityManager.find(T::class.java, reference.entityId)) {
            "Search result ${reference.entityType}/${reference.entityId} no longer exists"
        }
}
