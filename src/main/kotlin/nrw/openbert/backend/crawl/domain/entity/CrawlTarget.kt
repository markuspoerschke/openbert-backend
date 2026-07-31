package nrw.openbert.backend.crawl.domain.entity

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
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
    val createdAt: Instant = Instant.now(),
    var lastCrawledAt: Instant? = null,
    var lastIndexedAt: Instant? = null,
    var modifiedAt: Instant = Instant.now(),
    var referenceCount: Int = 0,
    var scheduledAt: Instant? = null,
    var jobId: UUID? = null,
) {
    companion object {
        fun create(resource: OparlResource): CrawlTarget = CrawlTarget(resource = resource)
    }

    fun increaseReferenceCounter() = apply {
        referenceCount++
        modifiedAt = Instant.now()
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
