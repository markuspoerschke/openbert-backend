package nrw.openbert.backend.parldata.adapter.inbound.scheduler

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
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
import nrw.openbert.backend.parldata.application.port.outbound.repository.ResolvedSystemRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicReference

@Component
class ResourceMetrics(
    registry: MeterRegistry,
    agendaItemRepository: ResolvedAgendaItemRepository,
    bodyRepository: ResolvedBodyRepository,
    consultationRepository: ResolvedConsultationRepository,
    fileRepository: ResolvedFileRepository,
    legislativeTermRepository: ResolvedLegislativeTermRepository,
    locationRepository: ResolvedLocationRepository,
    meetingRepository: ResolvedMeetingRepository,
    membershipRepository: ResolvedMembershipRepository,
    organizationRepository: ResolvedOrganizationRepository,
    paperRepository: ResolvedPaperRepository,
    personRepository: ResolvedPersonRepository,
    systemRepository: ResolvedSystemRepository,
) {
    private val repositories: Map<String, CrudRepository<*, *>> =
        mapOf(
            "agenda_item" to agendaItemRepository,
            "body" to bodyRepository,
            "consultation" to consultationRepository,
            "file" to fileRepository,
            "legislative_term" to legislativeTermRepository,
            "location" to locationRepository,
            "meeting" to meetingRepository,
            "membership" to membershipRepository,
            "organization" to organizationRepository,
            "paper" to paperRepository,
            "person" to personRepository,
            "system" to systemRepository,
        )
    private val counts = AtomicReference(repositories.mapValues { 0L })

    init {
        repositories.keys.forEach { resource ->
            Gauge
                .builder("oparl.resources", counts) { it.get().getValue(resource).toDouble() }
                .tag("resource", resource)
                .description("Number of stored resolved OParl resources")
                .register(registry)
        }
    }

    @Scheduled(fixedDelayString = "30s")
    fun refresh() {
        val refreshedCounts = repositories.mapValues { (_, repository) -> repository.count() }
        counts.set(refreshedCounts)
    }
}
