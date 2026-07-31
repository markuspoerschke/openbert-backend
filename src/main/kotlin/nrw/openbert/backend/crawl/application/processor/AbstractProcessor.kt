package nrw.openbert.backend.crawl.application.processor

import nrw.openbert.backend.crawl.application.CrawlTargetEnqueuer
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlObjectDto
import nrw.openbert.backend.oparl.domain.entity.AbstractOparlEntity
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import org.slf4j.LoggerFactory

abstract class AbstractProcessor<D : OparlObjectDto, E : AbstractOparlEntity>(
    private val crawlTargetEnqueuer: CrawlTargetEnqueuer,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    protected abstract fun findEntity(resource: OparlResource): E?
    protected abstract fun createEntity(resource: OparlResource): E
    protected abstract fun updateEntity(entity: E, dto: D)

    protected abstract fun saveEntity(entity: E): E

    open fun process(resource: OparlResource, dto: D): E {
        logger.info("Processing OParl resource {}", resource.url.value)

        val entity = findOrCreate(resource)
        updateEntity(entity, dto)
        saveEntity(entity)

        crawlTargetEnqueuer.markAsCrawled(resource)

        return entity
    }

    protected fun enqueueUrls(
        source: OparlResource,
        vararg urls: String?,
    ) {
        enqueueUrls(source, urls.asIterable())
    }

    protected fun enqueueUrls(
        source: OparlResource,
        urls: Iterable<String?>,
    ) {
        urls.filterNotNull()
            .map { source.withUrl(it) }
            .forEach(crawlTargetEnqueuer::enqueue)
    }

    protected fun <
        EmbeddedDto : OparlObjectDto,
        EmbeddedEntity : AbstractOparlEntity,
    > processInline(
        source: OparlResource,
        dto: EmbeddedDto?,
        processor: AbstractProcessor<EmbeddedDto, EmbeddedEntity>,
    ) {
        if (dto == null) {
            return
        }

        val url = requireNotNull(dto.id) { "Inline OParl object has no id" }
        processor.process(source.withUrl(url), dto)
    }

    protected fun <
        EmbeddedDto : OparlObjectDto,
        EmbeddedEntity : AbstractOparlEntity,
    > processInline(
        source: OparlResource,
        dtos: Iterable<EmbeddedDto>?,
        processor: AbstractProcessor<EmbeddedDto, EmbeddedEntity>,
    ) {
        dtos?.forEach { processInline(source, it, processor) }
    }

    private fun findOrCreate(resource: OparlResource): E {
        val entity = findEntity(resource)

        if (entity != null) {
            return entity
        }

        return createEntity(resource)
    }

    private fun OparlResource.withUrl(url: String) =
        OparlResource(system, OparlUrl(url))
}
