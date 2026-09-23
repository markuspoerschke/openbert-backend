package nrw.openbert.backend.parldata.domain.entity

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.parldata.domain.enums.CrawlEntityType
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "crawl_target",
    uniqueConstraints = [
        UniqueConstraint("uq_oparl_system_url", ["system_id", "url"])
    ]
)
class CrawlTarget(
    @Id
    var id: UUID = UUID.randomUUID(),
    @Embedded
    val resource: OparlResource,
    @Enumerated(EnumType.STRING)
    var entityType: CrawlEntityType? = null,
    val createdAt: Instant = Instant.now(),
    var lastCrawledAt: Instant? = null,
    var lastIndexedAt: Instant? = null,
    var modifiedAt: Instant = Instant.now(),
    var referenceCount: Int = 0,
    var scheduledAt: Instant? = null,
    var jobId: UUID? = null,
) {
    companion object {
        fun create(resource: OparlResource, entityType: CrawlEntityType? = null): CrawlTarget =
            CrawlTarget(resource = resource, entityType = entityType)
    }

    fun increaseReferenceCounter() = apply {
        referenceCount++
        modifiedAt = Instant.now()
    }

    fun assignEntityType(entityType: CrawlEntityType) = apply {
        if (this.entityType == null) {
            this.entityType = entityType
        }
    }

    fun markAsCrawled() = apply {
        modifiedAt = Instant.now()
        lastIndexedAt = modifiedAt
        referenceCount = 0
        scheduledAt = null
        jobId = null
        lastCrawledAt = modifiedAt
    }

    fun markAsScheduled(jobId: UUID) = apply {
        scheduledAt = null
        this.jobId = jobId
    }
}
