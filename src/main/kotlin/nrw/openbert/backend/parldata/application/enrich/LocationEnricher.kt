package nrw.openbert.backend.parldata.application.enrich

import nrw.openbert.backend.parldata.application.port.outbound.repository.EnrichedLocationInterface
import nrw.openbert.backend.parldata.application.port.outbound.repository.RawLocationRepository
import nrw.openbert.backend.parldata.domain.enrich.enricher.SimpleLocationEnricher
import nrw.openbert.backend.parldata.domain.entity.enriched.EnrichedLocation
import nrw.openbert.backend.parldata.domain.entity.raw.RawLocation
import nrw.openbert.backend.parldata.domain.enums.OparlType
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class LocationEnricher(
    private val rawLocationRepository: RawLocationRepository,
    private val enrichedLocationRepository: EnrichedLocationInterface,
    private val simpleEnricher: SimpleLocationEnricher,
) : EntityEnricher {
    override val entityType: OparlType
        get() = OparlType.LOCATION

    override fun enrich(rawEntityId: UUID) {
        val rawLocation = rawLocationRepository.findById(rawEntityId).orElseThrow {
            IllegalArgumentException("Raw location with id $rawEntityId not found")
        }

        val enrichedLocation = findOrCreateEnrichedEntity(rawLocation)
        val overrides = simpleEnricher.enrich(rawLocation, enrichedLocation.toLocationOverrides())

        enrichedLocation.applyValuesFrom(overrides)
        enrichedLocationRepository.save(enrichedLocation)
    }

    private fun findOrCreateEnrichedEntity(rawLocation: RawLocation): EnrichedLocation {
        return enrichedLocationRepository.findById(rawLocation.id).orElseGet {
            EnrichedLocation.fromRaw(rawLocation)
        }
    }
}
