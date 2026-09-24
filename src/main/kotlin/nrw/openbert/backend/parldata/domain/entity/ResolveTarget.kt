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
    name = "resolve_target",
    uniqueConstraints = [UniqueConstraint(columnNames = ["raw_entity_id", "entity_type"])],
)
class ResolveTarget(
    @Id
    val id: UUID = UUID.randomUUID(),
    val rawEntityId: UUID,
    @Enumerated(EnumType.STRING)
    val entityType: OparlType,
    val createdAt: Instant = Instant.now(),
    var lastResolvedAt: Instant? = null,
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

    fun markAsResolved() = apply {
        modifiedAt = Instant.now()
        lastResolvedAt = modifiedAt
        referenceCount = 0
        scheduledAt = null
        jobId = null
    }

    companion object {
        fun create(entity: AbstractOparlEntity) = ResolveTarget(
            rawEntityId = entity.id,
            entityType = entity.getType(),
        )
    }
}
