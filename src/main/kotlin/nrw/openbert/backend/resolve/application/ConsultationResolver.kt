package nrw.openbert.backend.resolve.application

import nrw.openbert.backend.resolve.domain.entity.ResolveEntityType
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
