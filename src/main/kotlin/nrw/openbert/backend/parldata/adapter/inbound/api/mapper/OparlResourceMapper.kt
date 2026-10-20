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
import java.util.UUID

@Component
class OparlResourceMapper(
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
    fun toDto(reference: OparlSearchReference, bodyId: UUID? = null): Any {
        return when (reference.entityType) {
            OparlType.BODY -> bodyMapper.toDto(find<ResolvedBody>(reference, bodyId))
            OparlType.LEGISLATIVE_TERM -> {
                legislativeTermMapper.toDto(find<ResolvedLegislativeTerm>(reference, bodyId))
            }
            OparlType.ORGANIZATION -> {
                organizationMapper.toDto(find<ResolvedOrganization>(reference, bodyId))
            }
            OparlType.PERSON -> personMapper.toDto(find<ResolvedPerson>(reference, bodyId))
            OparlType.MEMBERSHIP -> {
                membershipMapper.toDto(find<ResolvedMembership>(reference, bodyId))
            }
            OparlType.MEETING -> meetingMapper.toDto(find<ResolvedMeeting>(reference, bodyId))
            OparlType.AGENDA_ITEM -> {
                agendaItemMapper.toDto(find<ResolvedAgendaItem>(reference, bodyId))
            }
            OparlType.PAPER -> paperMapper.toDto(find<ResolvedPaper>(reference, bodyId))
            OparlType.CONSULTATION -> {
                consultationMapper.toDto(find<ResolvedConsultation>(reference, bodyId))
            }
            OparlType.FILE -> fileMapper.toDto(find<ResolvedFile>(reference, bodyId))
            OparlType.LOCATION -> locationMapper.toDto(find<ResolvedLocation>(reference, bodyId))
            OparlType.SYSTEM -> throw IllegalArgumentException("Source systems have no individual local resource ID")
        }
    }

    private inline fun <reified T : Any> find(reference: OparlSearchReference, bodyId: UUID?): T {
        val entity = if (bodyId == null) {
            entityManager.find(T::class.java, reference.entityId)
        } else {
            val entityName = entityManager.metamodel.entity(T::class.java).name
            entityManager.createQuery(
                "SELECT entity FROM $entityName entity WHERE entity.id = :id AND entity.body.id = :bodyId",
                T::class.java,
            )
                .setParameter("id", reference.entityId)
                .setParameter("bodyId", bodyId)
                .resultList.singleOrNull()
        }
        return requireNotNull(entity) {
            "OParl resource ${reference.entityType}/${reference.entityId} was not found for this ID"
        }
    }
}
