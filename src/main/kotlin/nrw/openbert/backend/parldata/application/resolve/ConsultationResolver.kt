package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.domain.enums.OparlType
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ConsultationResolver(
    private val resolvedEntityService: ResolvedEntityService,
) : EntityResolver {
    override val entityType = OparlType.CONSULTATION

    override fun resolve(rawEntityId: UUID) {
        resolvedEntityService.resolveConsultation(rawEntityId)
    }
}
