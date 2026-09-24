package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.domain.enums.OparlType
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class LocationResolver(
    private val resolvedEntityService: ResolvedEntityService,
) : EntityResolver {
    override val entityType = OparlType.LOCATION

    override fun resolve(rawEntityId: UUID) {
        resolvedEntityService.resolveLocation(rawEntityId)
    }
}
