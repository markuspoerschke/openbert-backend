package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.domain.enums.ResolveEntityType
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PersonResolver(
    private val resolvedEntityService: ResolvedEntityService,
) : EntityResolver {
    override val entityType = ResolveEntityType.PERSON

    override fun resolve(rawEntityId: UUID) {
        resolvedEntityService.resolvePerson(rawEntityId)
    }
}
