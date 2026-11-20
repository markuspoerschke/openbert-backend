package nrw.openbert.backend.parldata.domain.entity

import java.time.Instant
import java.util.UUID

interface QueueTarget {
    val id: UUID

    val failedAt: Instant?

    val failureReason: String?

    fun markAsFailed(reason: String): QueueTarget
}
