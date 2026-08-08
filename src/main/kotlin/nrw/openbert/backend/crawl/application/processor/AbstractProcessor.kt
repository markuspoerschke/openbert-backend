package nrw.openbert.backend.crawl.application.processor

import nrw.openbert.backend.crawl.application.CrawlTargetEnqueuer
import nrw.openbert.backend.crawl.domain.entity.CrawlEntityType
import nrw.openbert.backend.crawl.infrastructure.dto.oparl.OparlObjectDto
import nrw.openbert.backend.resolve.application.ResolveTargetEnqueuer
import nrw.openbert.backend.oparl.domain.entity.AbstractOparlEntity
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

abstract class AbstractProcessor<D : OparlObjectDto, E : AbstractOparlEntity>(
    private val crawlTargetEnqueuer: CrawlTargetEnqueuer,
    private val resolveTargetEnqueuer: ResolveTargetEnqueuer,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    protected abstract fun findEntity(resource: OparlResource): E?
    protected abstract fun createEntity(resource: OparlResource): E
    protected abstract fun updateEntity(entity: E, dto: D)

    protected abstract fun saveEntity(entity: E): E

    @Transactional
    open fun process(resource: OparlResource, dto: D): E {
        logger.info("Processing OParl resource {}", resource.url.value)

        val entity = findOrCreate(resource)
        updateEntity(entity, dto)
        val savedEntity = saveEntity(entity)

        resolveTargetEnqueuer.enqueue(savedEntity)
        crawlTargetEnqueuer.markAsCrawled(resource)

        return savedEntity
    }

    protected fun enqueueUrls(
        source: OparlResource,
        entityType: CrawlEntityType,
        vararg urls: String?,
    ) {
        enqueueUrls(source, entityType, urls.asIterable())
    }

    protected fun enqueueUrls(
        source: OparlResource,
        entityType: CrawlEntityType,
        urls: Iterable<String?>,
    ) {
        urls.filterNotNull()
            .map { source.withUrl(it) }
            .forEach { crawlTargetEnqueuer.enqueue(it, entityType) }
    }

    protected fun <
        EmbeddedDto : OparlObjectDto,
        EmbeddedEntity : AbstractOparlEntity,
    > processInline(
        source: OparlResource,
        dto: EmbeddedDto?,
        processor: AbstractProcessor<EmbeddedDto, EmbeddedEntity>,
    ): EmbeddedEntity? {
        if (dto == null) {
            return null
        }

        val url = requireNotNull(dto.id) { "Inline OParl object has no id" }
        return processor.process(source.withUrl(url), dto)
    }

    protected fun <
        EmbeddedDto : OparlObjectDto,
        EmbeddedEntity : AbstractOparlEntity,
    > processInline(
        source: OparlResource,
        dtos: Iterable<EmbeddedDto>?,
        processor: AbstractProcessor<EmbeddedDto, EmbeddedEntity>,
    ): List<EmbeddedEntity> = dtos?.mapNotNull { processInline(source, it, processor) }.orEmpty()

    protected fun MutableList<String>.addUrl(url: OparlUrl) {
        if (url.value !in this) add(url.value)
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
