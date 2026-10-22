package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.application.port.outbound.repository.EnrichedLocationInterface
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawLocationRepository
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedLocationRepository
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedLocation
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class LocationResolver(
    private val rawRepository: RawLocationRepository,
    private val resolvedRepository: ResolvedLocationRepository,
    private val enrichedLocationRepository: EnrichedLocationInterface,
    private val bodyResolver: BodyResolver,
    private val organizationResolver: OrganizationResolver,
    private val personResolver: PersonResolver,
    private val meetingResolver: MeetingResolver,
    private val paperResolver: PaperResolver,
) : AbstractEntityResolver() {
    override val entityType = OparlType.LOCATION

    override fun resolve(rawEntityId: UUID) {
        val raw = rawRepository.findById(rawEntityId).orElseThrow {
            IllegalArgumentException("Raw location with id $rawEntityId does not exist")
        }
        val bodies = buildList {
            raw.bodyUrls.mapTo(this) { bodyResolver.resolve(relatedResource(raw.resource, OparlUrl(it))) }
            raw.organizationUrls.mapTo(this) { organizationResolver.resolve(relatedResource(raw.resource, OparlUrl(it))).body }
            raw.personUrls.mapTo(this) { personResolver.resolve(relatedResource(raw.resource, OparlUrl(it))).body }
            raw.meetingUrls.mapTo(this) { meetingResolver.resolve(relatedResource(raw.resource, OparlUrl(it))).body }
            raw.paperUrls.mapTo(this) { paperResolver.resolve(relatedResource(raw.resource, OparlUrl(it))).body }
        }
        val body = commonBody(raw.resource, bodies)
        val resolved = resolvedRepository.findByResource(raw.resource)
            ?: ResolvedLocation.fromRaw(raw, body)

        requireRelation("body", raw.resource, resolved.body.id, body.id)
        resolved.applyValuesFrom(raw)
        enrichedLocationRepository.findById(raw.id).ifPresent {
            resolved.applyOverrides(it.toLocationOverrides())
        }
        resolvedRepository.save(resolved)
    }
}
