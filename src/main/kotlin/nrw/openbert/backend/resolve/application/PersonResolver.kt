package nrw.openbert.backend.resolve.application

import nrw.openbert.backend.resolve.domain.entity.ResolveEntityType
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
