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
            ORDER BY CASE target.entityType
                WHEN nrw.openbert.backend.resolve.domain.entity.ResolveEntityType.SYSTEM THEN 0
                WHEN nrw.openbert.backend.resolve.domain.entity.ResolveEntityType.BODY THEN 1
                WHEN nrw.openbert.backend.resolve.domain.entity.ResolveEntityType.ORGANIZATION THEN 2
                WHEN nrw.openbert.backend.resolve.domain.entity.ResolveEntityType.PAPER THEN 2
                WHEN nrw.openbert.backend.resolve.domain.entity.ResolveEntityType.LEGISLATIVE_TERM THEN 2
                WHEN nrw.openbert.backend.resolve.domain.entity.ResolveEntityType.PERSON THEN 3
                WHEN nrw.openbert.backend.resolve.domain.entity.ResolveEntityType.MEETING THEN 4
                WHEN nrw.openbert.backend.resolve.domain.entity.ResolveEntityType.MEMBERSHIP THEN 4
                WHEN nrw.openbert.backend.resolve.domain.entity.ResolveEntityType.AGENDA_ITEM THEN 5
                WHEN nrw.openbert.backend.resolve.domain.entity.ResolveEntityType.FILE THEN 6
                WHEN nrw.openbert.backend.resolve.domain.entity.ResolveEntityType.CONSULTATION THEN 7
                WHEN nrw.openbert.backend.resolve.domain.entity.ResolveEntityType.LOCATION THEN 7
                ELSE 8
            END,
            target.referenceCount DESC,
            target.createdAt ASC
        """,
    )
    fun findDue(
        @Param("now") now: Instant,
        @Param("entityTypes") entityTypes: Collection<ResolveEntityType>,
        pageable: Pageable,
    ): List<ResolveTarget>
}
