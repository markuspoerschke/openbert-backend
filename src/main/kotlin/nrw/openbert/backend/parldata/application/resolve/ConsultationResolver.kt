package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.domain.enums.ResolveEntityType
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ConsultationResolver(
    private val resolvedEntityService: ResolvedEntityService,
) : EntityResolver {
    override val entityType = ResolveEntityType.CONSULTATION

    override fun resolve(rawEntityId: UUID) {
        resolvedEntityService.resolveConsultation(rawEntityId)
    }
}
