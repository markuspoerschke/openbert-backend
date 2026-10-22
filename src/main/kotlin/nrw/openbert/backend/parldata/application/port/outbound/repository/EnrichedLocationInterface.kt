package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.enriched.EnrichedLocation
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface EnrichedLocationInterface : JpaRepository<EnrichedLocation, UUID>
