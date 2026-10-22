package nrw.openbert.backend.parldata.adapter.inbound.crawler.processor

import nrw.openbert.backend.parldata.application.crawl.CrawlTargetEnqueuer
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.adapter.outbound.client.oparl.dto.OparlObjectDto
import nrw.openbert.backend.parldata.application.enrich.EnrichTargetEnqueuer
import nrw.openbert.backend.parldata.domain.entity.AbstractOparlEntity
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

abstract class AbstractProcessor<D : OparlObjectDto, E : AbstractOparlEntity>(
    private val crawlTargetEnqueuer: CrawlTargetEnqueuer,
    private val enrichTargetEnqueuer: EnrichTargetEnqueuer,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    protected abstract fun findEntity(resource: OparlResource): E?
    protected abstract fun createEntity(resource: OparlResource): E
    protected abstract fun updateEntity(entity: E, dto: D)

    protected abstract fun saveEntity(entity: E): E
    protected abstract fun processRelationships(resource: OparlResource, dto: D)

    @Transactional
    open fun process(resource: OparlResource, dto: D): E {
        logger.info("Processing OParl resource {}", resource.url.value)

        val entity = findEntity(resource) ?: createEntity(resource)
        processRelationships(resource, dto)
        updateEntity(entity, dto)
        val savedEntity = saveEntity(entity)

        enrichTargetEnqueuer.enqueue(savedEntity)
        crawlTargetEnqueuer.markAsCrawled(resource)

        return savedEntity
    }

    /**
     * List representations may omit fields, including references to their parent.
     * Use them only to seed new entities; only fetching the entity URL completes a crawl.
     */
    @Transactional
    open fun processEmbedded(resource: OparlResource, dto: D, initialize: (E) -> Unit = {}): E {
        findEntity(resource)?.let { return it }

        val entity = createEntity(resource)
        processRelationships(resource, dto)
        updateEntity(entity, dto)
        initialize(entity)
        val savedEntity = saveEntity(entity)

        enrichTargetEnqueuer.enqueue(savedEntity)
        crawlTargetEnqueuer.enqueue(resource, savedEntity.getType())

        return savedEntity
    }

    protected fun enqueueUrls(
        source: OparlResource,
        entityType: OparlType,
        vararg urls: String?,
    ) {
        enqueueUrls(source, entityType, urls.asIterable())
    }

    protected fun enqueueUrls(
        source: OparlResource,
        entityType: OparlType,
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
        initialize: (EmbeddedEntity) -> Unit,
    ): EmbeddedEntity? {
        if (dto == null) {
            return null
        }

        val url = requireNotNull(dto.id) { "Inline OParl object has no id" }
        return processor.processEmbedded(source.withUrl(url), dto, initialize)
    }

    protected fun <
        EmbeddedDto : OparlObjectDto,
        EmbeddedEntity : AbstractOparlEntity,
    > processInline(
        source: OparlResource,
        dtos: Iterable<EmbeddedDto>?,
        processor: AbstractProcessor<EmbeddedDto, EmbeddedEntity>,
        initialize: (EmbeddedEntity) -> Unit,
    ): List<EmbeddedEntity> = dtos?.mapNotNull { processInline(source, it, processor, initialize) }.orEmpty()
}

private fun OparlResource.withUrl(url: String) = OparlResource(system, OparlUrl(url))

internal fun MutableList<String>.addUrl(url: OparlUrl) {
    if (url.value !in this) add(url.value)
}
