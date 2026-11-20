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
    override val id: UUID = UUID.randomUUID(),
    val rawEntityId: UUID,
    @Enumerated(EnumType.STRING)
    val entityType: OparlType,
    val createdAt: Instant = Instant.now(),
    var lastEnrichedAt: Instant? = null,
    var modifiedAt: Instant = Instant.now(),
    var referenceCount: Int = 0,
    var scheduledAt: Instant? = null,
    override var failedAt: Instant? = null,
    override var failureReason: String? = null,
) : QueueTarget {
    fun enqueue() =
        apply {
            referenceCount++
            modifiedAt = Instant.now()
        }

    fun markAsEnriched() =
        apply {
            modifiedAt = Instant.now()
            lastEnrichedAt = modifiedAt
            referenceCount = 0
            scheduledAt = null
            failedAt = null
            failureReason = null
        }

    override fun markAsFailed(reason: String): EnrichTarget =
        apply {
            modifiedAt = Instant.now()
            scheduledAt = null
            failedAt = modifiedAt
            failureReason = reason.take(FAILURE_REASON_MAX_LENGTH)
        }

    companion object {
        const val FAILURE_REASON_MAX_LENGTH = 1024

        fun create(entity: AbstractOparlEntity) =
            EnrichTarget(
                rawEntityId = entity.id,
                entityType = entity.getType(),
            )
    }
}
