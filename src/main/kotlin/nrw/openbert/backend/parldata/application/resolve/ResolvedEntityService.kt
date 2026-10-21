package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.application.exception.UnresolvedRelationException
import nrw.openbert.backend.parldata.application.port.outbound.repository.EnrichedLocationInterface
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawAgendaItem
import nrw.openbert.backend.parldata.domain.entity.raw.RawMeeting
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
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawAgendaItemRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawConsultationRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawFileRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawLegislativeTermRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawLocationRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawMeetingRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawMembershipRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawOrganizationRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawPaperRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawPersonRepository
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedAgendaItemRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedBodyRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedConsultationRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedFileRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedLegislativeTermRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedLocationRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedMeetingRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedMembershipRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedOrganizationRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedPaperRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedPersonRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ResolvedEntityService(
    private val rawAgendaItemRepository: RawAgendaItemRepository,
    private val rawBodyRepository: RawBodyRepository,
    private val rawConsultationRepository: RawConsultationRepository,
    private val rawFileRepository: RawFileRepository,
    private val rawLegislativeTermRepository: RawLegislativeTermRepository,
    private val rawLocationRepository: RawLocationRepository,
    private val enrichedLocationRepository: EnrichedLocationInterface,
    private val rawMeetingRepository: RawMeetingRepository,
    private val rawMembershipRepository: RawMembershipRepository,
    private val rawOrganizationRepository: RawOrganizationRepository,
    private val rawPaperRepository: RawPaperRepository,
    private val rawPersonRepository: RawPersonRepository,
    private val resolvedAgendaItemRepository: ResolvedAgendaItemRepository,
    private val resolvedBodyRepository: ResolvedBodyRepository,
    private val resolvedConsultationRepository: ResolvedConsultationRepository,
    private val resolvedFileRepository: ResolvedFileRepository,
    private val resolvedLegislativeTermRepository: ResolvedLegislativeTermRepository,
    private val resolvedLocationRepository: ResolvedLocationRepository,
    private val resolvedMeetingRepository: ResolvedMeetingRepository,
    private val resolvedMembershipRepository: ResolvedMembershipRepository,
    private val resolvedOrganizationRepository: ResolvedOrganizationRepository,
    private val resolvedPaperRepository: ResolvedPaperRepository,
    private val resolvedPersonRepository: ResolvedPersonRepository,
) {
    fun resolveAgendaItem(id: UUID): ResolvedAgendaItem = resolveAgendaItem(rawAgendaItemRepository.findById(id).orElseThrow {
        IllegalArgumentException("Raw agenda item with id $id does not exist")
    })

    fun resolveConsultation(id: UUID): ResolvedConsultation {
        val raw = rawConsultationRepository.findById(id).orElseThrow {
            IllegalArgumentException("Raw consultation with id $id does not exist")
        }
        val paper = resolvePaper(relatedResource(raw.resource, required(raw.paperUrl, "paper", raw.resource)))
        val agendaItem = resolveAgendaItem(relatedResource(raw.resource, required(raw.agendaItemUrl, "agenda item", raw.resource)))
        val meeting = resolveMeeting(relatedResource(raw.resource, required(raw.meetingUrl, "meeting", raw.resource)))
        val organizations = raw.organizationUrls.map { resolveOrganization(relatedResource(raw.resource, OparlUrl(it))) }
        val body = commonBody(raw.resource, listOf(paper.body, agendaItem.body, meeting.body) + organizations.map { it.body })
        val resolved = resolvedConsultationRepository.findByResource(raw.resource)
            ?: ResolvedConsultation.fromRaw(raw, body, paper, agendaItem, meeting, organizations)

        requireRelation("body", raw.resource, resolved.body.id, body.id)
        requireRelation("paper", raw.resource, resolved.paper.id, paper.id)
        requireRelation("agenda item", raw.resource, resolved.agendaItem.id, agendaItem.id)
        requireRelation("meeting", raw.resource, resolved.meeting.id, meeting.id)
        resolved.applyValuesFrom(raw)
        resolved.applyRelationsFrom(organizations)
        return resolvedConsultationRepository.save(resolved)
    }

    fun resolveFile(id: UUID): ResolvedFile = resolveFile(rawFileRepository.findById(id).orElseThrow {
        IllegalArgumentException("Raw file with id $id does not exist")
    }.resource)

    fun resolveLegislativeTerm(id: UUID): ResolvedLegislativeTerm {
        val raw = rawLegislativeTermRepository.findById(id).orElseThrow {
            IllegalArgumentException("Raw legislative term with id $id does not exist")
        }
        val body = resolveBody(relatedResource(raw.resource, required(raw.bodyUrl, "body", raw.resource)))
        val resolved = resolvedLegislativeTermRepository.findByResource(raw.resource)
            ?: ResolvedLegislativeTerm.fromRaw(raw, body)

        requireRelation("body", raw.resource, resolved.body.id, body.id)
        resolved.applyValuesFrom(raw)
        return resolvedLegislativeTermRepository.save(resolved)
    }

    fun resolveLocation(id: UUID): ResolvedLocation {
        val raw = rawLocationRepository.findById(id).orElseThrow {
            IllegalArgumentException("Raw location with id $id does not exist")
        }
        val bodies = buildList {
            raw.bodyUrls.mapTo(this) { resolveBody(relatedResource(raw.resource, OparlUrl(it))) }
            raw.organizationUrls.mapTo(this) { resolveOrganization(relatedResource(raw.resource, OparlUrl(it))).body }
            raw.personUrls.mapTo(this) { resolvePerson(relatedResource(raw.resource, OparlUrl(it))).body }
            raw.meetingUrls.mapTo(this) { resolveMeeting(relatedResource(raw.resource, OparlUrl(it))).body }
            raw.paperUrls.mapTo(this) { resolvePaper(relatedResource(raw.resource, OparlUrl(it))).body }
        }
        val body = commonBody(raw.resource, bodies)
        val resolved = resolvedLocationRepository.findByResource(raw.resource)
            ?: ResolvedLocation.fromRaw(raw, body)

        requireRelation("body", raw.resource, resolved.body.id, body.id)
        resolved.applyValuesFrom(raw)
        enrichedLocationRepository.findById(raw.id).ifPresent {
            resolved.applyOverrides(it.toLocationOverrides())
        }
        return resolvedLocationRepository.save(resolved)
    }

    fun resolveMeeting(id: UUID): ResolvedMeeting {
        val raw = rawMeetingRepository.findById(id).orElseThrow {
            IllegalArgumentException("Raw meeting with id $id does not exist")
        }
        return resolveMeeting(raw)
    }

    fun resolveMembership(id: UUID): ResolvedMembership {
        val raw = rawMembershipRepository.findById(id).orElseThrow {
            IllegalArgumentException("Raw membership with id $id does not exist")
        }
        val person = resolvePerson(relatedResource(raw.resource, required(raw.personUrl, "person", raw.resource)))
        val organization = resolveOrganization(relatedResource(raw.resource, required(raw.organizationUrl, "organization", raw.resource)))
        val onBehalfOf = raw.onBehalfOfUrl?.let { resolveOrganization(relatedResource(raw.resource, it)) }
        val body = commonBody(raw.resource, listOfNotNull(person.body, organization.body, onBehalfOf?.body))
        val resolved = resolvedMembershipRepository.findByResource(raw.resource)
            ?: ResolvedMembership.fromRaw(raw, body, person, organization, onBehalfOf)

        requireRelation("body", raw.resource, resolved.body.id, body.id)
        requireRelation("person", raw.resource, resolved.person.id, person.id)
        requireRelation("organization", raw.resource, resolved.organization.id, organization.id)
        resolved.applyValuesFrom(raw)
        resolved.applyRelationsFrom(onBehalfOf)
        return resolvedMembershipRepository.save(resolved)
    }

    fun resolvePaper(id: UUID): ResolvedPaper = resolvePaper(rawPaperRepository.findById(id).orElseThrow {
        IllegalArgumentException("Raw paper with id $id does not exist")
    }.resource)

    fun resolvePerson(id: UUID): ResolvedPerson = resolvePerson(rawPersonRepository.findById(id).orElseThrow {
        IllegalArgumentException("Raw person with id $id does not exist")
    }.resource)

    private fun resolveAgendaItem(raw: RawAgendaItem): ResolvedAgendaItem {
        val meeting = resolveMeeting(relatedResource(raw.resource, required(raw.meetingUrl, "meeting", raw.resource)))
        val resolutionFile = raw.resolutionFileUrl?.let { resolveFile(relatedResource(raw.resource, it), meeting.body) }
        val auxiliaryFiles = raw.auxiliaryFileUrls.map {
            resolveFile(relatedResource(raw.resource, OparlUrl(it)), meeting.body)
        }
        val resolved = resolvedAgendaItemRepository.findByResource(raw.resource)
            ?: ResolvedAgendaItem.fromRaw(raw, meeting.body, meeting, resolutionFile, auxiliaryFiles)

        requireRelation("body", raw.resource, resolved.body.id, meeting.body.id)
        requireRelation("meeting", raw.resource, resolved.meeting.id, meeting.id)
        resolved.applyValuesFrom(raw)
        resolved.applyRelationsFrom(resolutionFile, auxiliaryFiles)
        return resolvedAgendaItemRepository.save(resolved)
    }

    private fun resolveAgendaItem(resource: OparlResource): ResolvedAgendaItem {
        val raw = rawAgendaItemRepository.findByResource(resource)
        if (raw != null) return resolveAgendaItem(raw)
        return resolvedAgendaItemRepository.findByResource(resource)
            ?: throw notCrawled("agenda item", resource)
    }

    private fun resolveBody(resource: OparlResource): ResolvedBody {
        val raw = rawBodyRepository.findByResource(resource)
        if (raw == null) {
            return resolvedBodyRepository.findByResource(resource) ?: throw notCrawled("body", resource)
        }

        val resolved = resolvedBodyRepository.findByResource(resource) ?: ResolvedBody.fromRaw(raw)
        resolved.applyValuesFrom(raw)
        return resolvedBodyRepository.save(resolved)
    }

    private fun resolveFile(resource: OparlResource, bodyHint: ResolvedBody? = null): ResolvedFile {
        val existing = resolvedFileRepository.findByResource(resource)
        val raw = rawFileRepository.findByResource(resource)
            ?: return existing ?: throw notCrawled("file", resource)
        val bodies = buildList {
            (bodyHint ?: existing?.body)?.let(::add)
            raw.meetingUrls.mapTo(this) { resolveMeeting(relatedResource(raw.resource, OparlUrl(it))).body }
            raw.paperUrls.mapTo(this) { resolvePaper(relatedResource(raw.resource, OparlUrl(it))).body }
            raw.agendaItemUrls.mapTo(this) { bodyForAgendaItem(relatedResource(raw.resource, OparlUrl(it))) }
        }
        val body = commonBody(raw.resource, bodies)
        val resolved = existing ?: ResolvedFile.fromRaw(raw, body)
        requireRelation("body", raw.resource, resolved.body.id, body.id)
        resolved.applyValuesFrom(raw)
        return resolvedFileRepository.save(resolved)
    }

    private fun resolveMeeting(resource: OparlResource): ResolvedMeeting {
        val raw = rawMeetingRepository.findByResource(resource)
        if (raw == null) {
            return resolvedMeetingRepository.findByResource(resource) ?: throw notCrawled("meeting", resource)
        }

        return resolveMeeting(raw)
    }

    private fun resolveMeeting(raw: RawMeeting): ResolvedMeeting {
        val existing = resolvedMeetingRepository.findByResource(raw.resource)
        val organizations = raw.organizationUrls.map { url ->
            resolveOrganization(relatedResource(raw.resource, OparlUrl(url)))
        }
        val body = resolveMeetingBody(raw, organizations, existing?.body)
        val resolved = existing ?: ResolvedMeeting(raw.resource, body)

        requireRelation("body", raw.resource, resolved.body.id, body.id)
        // Reapply the latest raw snapshot on every run, including for existing meetings.
        resolved.applyValuesFrom(raw)
        resolved.applyRelationsFrom(organizations)
        return resolvedMeetingRepository.save(resolved)
    }

    private fun resolveMeetingBody(
        raw: RawMeeting,
        organizations: List<ResolvedOrganization>,
        existingBody: ResolvedBody?,
    ): ResolvedBody {
        val candidateBodies = mutableListOf<ResolvedBody>()
        val bodyUrl = raw.bodyUrl
        if (bodyUrl != null) {
            candidateBodies.add(resolveBody(relatedResource(raw.resource, bodyUrl)))
        }
        candidateBodies.addAll(organizations.map { it.body })
        for (participantUrl in raw.participantUrls) {
            val participantResource = relatedResource(raw.resource, OparlUrl(participantUrl))
            val participant = resolvePerson(participantResource)
            candidateBodies.add(participant.body)
        }

        // Use the known body only when the current raw snapshot provides no body information.
        if (candidateBodies.isEmpty() && existingBody != null) {
            return resolveBody(existingBody.resource)
        }
        return commonBody(raw.resource, candidateBodies)
    }

    private fun resolveOrganization(resource: OparlResource): ResolvedOrganization {
        val raw = rawOrganizationRepository.findByResource(resource)
            ?: return resolvedOrganizationRepository.findByResource(resource) ?: throw notCrawled("organization", resource)
        val body = resolveBody(relatedResource(raw.resource, required(raw.bodyUrl, "body", raw.resource)))
        val resolved = resolvedOrganizationRepository.findByResource(resource)
            ?: ResolvedOrganization.fromRaw(raw, body)

        requireRelation("body", raw.resource, resolved.body.id, body.id)
        resolved.applyValuesFrom(raw)
        return resolvedOrganizationRepository.save(resolved)
    }

    private fun resolvePaper(resource: OparlResource): ResolvedPaper {
        val raw = rawPaperRepository.findByResource(resource)
            ?: return resolvedPaperRepository.findByResource(resource) ?: throw notCrawled("paper", resource)
        val body = resolveBody(relatedResource(raw.resource, required(raw.bodyUrl, "body", raw.resource)))
        val resolved = resolvedPaperRepository.findByResource(resource) ?: ResolvedPaper.fromRaw(raw, body)

        requireRelation("body", raw.resource, resolved.body.id, body.id)
        resolved.applyValuesFrom(raw)
        return resolvedPaperRepository.save(resolved)
    }

    private fun resolvePerson(resource: OparlResource): ResolvedPerson {
        val raw = rawPersonRepository.findByResource(resource)
            ?: return resolvedPersonRepository.findByResource(resource) ?: throw notCrawled("person", resource)
        val body = resolveBody(relatedResource(raw.resource, required(raw.bodyUrl, "body", raw.resource)))
        val resolved = resolvedPersonRepository.findByResource(resource) ?: ResolvedPerson.fromRaw(raw, body)

        requireRelation("body", raw.resource, resolved.body.id, body.id)
        resolved.applyValuesFrom(raw)
        return resolvedPersonRepository.save(resolved)
    }

    private fun bodyForAgendaItem(resource: OparlResource): ResolvedBody {
        resolvedAgendaItemRepository.findByResource(resource)?.let { return it.body }
        val raw = rawAgendaItemRepository.findByResource(resource) ?: throw notCrawled("agenda item", resource)
        return resolveMeeting(relatedResource(raw.resource, required(raw.meetingUrl, "meeting", raw.resource))).body
    }

    private fun commonBody(source: OparlResource, bodies: Collection<ResolvedBody>): ResolvedBody {
        val distinct = bodies.distinctBy { it.id }
        if (distinct.isEmpty()) {
            throw UnresolvedRelationException("Missing body relation on ${source.url.value}")
        }
        if (distinct.size > 1) {
            throw UnresolvedRelationException("Conflicting body relations on ${source.url.value}")
        }
        return distinct.single()
    }

    private fun required(url: OparlUrl?, relation: String, source: OparlResource): OparlUrl =
        url ?: throw UnresolvedRelationException("Missing $relation relation on ${source.url.value}")

    private fun requireRelation(relation: String, source: OparlResource, actualId: UUID, expectedId: UUID) {
        if (actualId != expectedId) {
            throw UnresolvedRelationException("Conflicting $relation relation on ${source.url.value}")
        }
    }

    private fun relatedResource(source: OparlResource, url: OparlUrl) = OparlResource(source.system, url)

    private fun notCrawled(type: String, resource: OparlResource) =
        UnresolvedRelationException("Referenced $type ${resource.url.value} has not been crawled")
}
