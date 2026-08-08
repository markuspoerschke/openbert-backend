package nrw.openbert.backend.resolve.domain.repository

import nrw.openbert.backend.resolve.domain.entity.ResolveEntityType
import nrw.openbert.backend.resolve.domain.entity.ResolveTarget
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.util.UUID

interface ResolveTargetRepository : JpaRepository<ResolveTarget, UUID> {
    fun findByRawEntityIdAndEntityType(rawEntityId: UUID, entityType: ResolveEntityType): ResolveTarget?

    @Query(
        """
            SELECT target
            FROM ResolveTarget target
            WHERE target.referenceCount > 0
              AND (target.scheduledAt <= :now OR target.scheduledAt IS NULL)
              AND target.jobId IS NULL
              AND target.entityType IN :entityTypes
            ORDER BY target.referenceCount DESC, target.createdAt ASC
        """,
    )
    fun findDue(
        @Param("now") now: Instant,
        @Param("entityTypes") entityTypes: Collection<ResolveEntityType>,
        pageable: Pageable,
    ): List<ResolveTarget>
}
