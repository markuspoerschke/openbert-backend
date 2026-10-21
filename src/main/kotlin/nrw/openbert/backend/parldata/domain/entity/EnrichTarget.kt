package nrw.openbert.backend.parldata.domain.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import nrw.openbert.backend.parldata.domain.enums.OparlType
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "enrich_target",
    uniqueConstraints = [UniqueConstraint(columnNames = ["raw_entity_id", "entity_type"])],
)
class EnrichTarget(
    @Id
    val id: UUID = UUID.randomUUID(),
    val rawEntityId: UUID,
    @Enumerated(EnumType.STRING)
    val entityType: OparlType,
    val createdAt: Instant = Instant.now(),
    var lastEnrichedAt: Instant? = null,
    var modifiedAt: Instant = Instant.now(),
    var referenceCount: Int = 0,
    var scheduledAt: Instant? = null,
    var jobId: UUID? = null,
) {
    fun enqueue() = apply {
        referenceCount++
        modifiedAt = Instant.now()
    }

    fun markAsScheduled(jobId: UUID) = apply {
        scheduledAt = Instant.now()
        this.jobId = jobId
    }

    fun markAsEnriched() = apply {
        modifiedAt = Instant.now()
        lastEnrichedAt = modifiedAt
        referenceCount = 0
        scheduledAt = null
        jobId = null
    }

    companion object {
        fun create(entity: AbstractOparlEntity) = EnrichTarget(
            rawEntityId = entity.id,
            entityType = entity.getType(),
        )
    }
}
