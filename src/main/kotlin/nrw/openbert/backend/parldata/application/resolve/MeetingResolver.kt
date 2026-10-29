package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.application.port.outbound.repository.RawMeetingRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedMeetingRepository
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.raw.RawMeeting
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedBody
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedMeeting
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedOrganization
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MeetingResolver(
    private val rawRepository: RawMeetingRepository,
    private val resolvedRepository: ResolvedMeetingRepository,
    private val bodyResolver: BodyResolver,
    private val organizationResolver: OrganizationResolver,
    private val personResolver: PersonResolver,
) : AbstractEntityResolver() {
    override val entityType = OparlType.MEETING

    override fun resolve(rawEntityId: UUID) {
        val raw =
            rawRepository.findById(rawEntityId).orElseThrow {
                IllegalArgumentException("Raw meeting with id $rawEntityId does not exist")
            }
        resolve(raw)
    }

    fun resolve(resource: OparlResource): ResolvedMeeting {
        val raw = rawRepository.findByResource(resource)
        if (raw == null) {
            return resolvedRepository.findByResource(resource) ?: throw notCrawled("meeting", resource)
        }

        return resolve(raw)
    }

    private fun resolve(raw: RawMeeting): ResolvedMeeting {
        val existing = resolvedRepository.findByResource(raw.resource)
        val organizations =
            raw.organizationUrls.map { url ->
                organizationResolver.resolve(relatedResource(raw.resource, OparlUrl(url)))
            }
        val body = resolveMeetingBody(raw, organizations, existing?.body)
        val resolved = existing ?: ResolvedMeeting(raw.resource, body)

        requireRelation("body", raw.resource, resolved.body.id, body.id)
        // Reapply the latest raw snapshot on every run, including for existing meetings.
        resolved.applyValuesFrom(raw)
        resolved.applyRelationsFrom(organizations)
        return resolvedRepository.save(resolved)
    }

    private fun resolveMeetingBody(
        raw: RawMeeting,
        organizations: List<ResolvedOrganization>,
        existingBody: ResolvedBody?,
    ): ResolvedBody {
        val candidateBodies = mutableListOf<ResolvedBody>()
        val bodyUrl = raw.bodyUrl
        if (bodyUrl != null) {
            candidateBodies.add(bodyResolver.resolve(relatedResource(raw.resource, bodyUrl)))
        }
        candidateBodies.addAll(organizations.map { it.body })
        for (participantUrl in raw.participantUrls) {
            val participantResource = relatedResource(raw.resource, OparlUrl(participantUrl))
            val participant = personResolver.resolve(participantResource)
            candidateBodies.add(participant.body)
        }

        // Use the known body only when the current raw snapshot provides no body information.
        if (candidateBodies.isEmpty() && existingBody != null) {
            return bodyResolver.resolve(existingBody.resource)
        }
        return commonBody(raw.resource, candidateBodies)
    }
}
