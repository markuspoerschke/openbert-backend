package nrw.openbert.backend.parldata.application.resolve

import nrw.openbert.backend.parldata.application.exception.UnresolvedRelationException
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.entity.resolved.ResolvedBody
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import java.util.UUID

abstract class AbstractEntityResolver : EntityResolver {
    protected fun commonBody(
        source: OparlResource,
        bodies: Collection<ResolvedBody>,
    ): ResolvedBody {
        val distinct = bodies.distinctBy { it.id }
        if (distinct.isEmpty()) {
            throw UnresolvedRelationException("Missing body relation on ${source.url.value}")
        }
        if (distinct.size > 1) {
            throw UnresolvedRelationException("Conflicting body relations on ${source.url.value}")
        }
        return distinct.single()
    }

    protected fun required(
        url: OparlUrl?,
        relation: String,
        source: OparlResource,
    ): OparlUrl = url ?: throw UnresolvedRelationException("Missing $relation relation on ${source.url.value}")

    protected fun requireRelation(
        relation: String,
        source: OparlResource,
        actualId: UUID,
        expectedId: UUID,
    ) {
        if (actualId != expectedId) {
            throw UnresolvedRelationException("Conflicting $relation relation on ${source.url.value}")
        }
    }

    protected fun relatedResource(
        source: OparlResource,
        url: OparlUrl,
    ) = OparlResource(source.system, url)

    protected fun notCrawled(
        type: String,
        resource: OparlResource,
    ) = UnresolvedRelationException("Referenced $type ${resource.url.value} has not been crawled")
}
