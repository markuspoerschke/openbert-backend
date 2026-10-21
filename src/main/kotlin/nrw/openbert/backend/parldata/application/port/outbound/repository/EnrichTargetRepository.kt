package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.EnrichTarget
import nrw.openbert.backend.parldata.domain.enums.OparlType
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.util.UUID

interface EnrichTargetRepository : JpaRepository<EnrichTarget, UUID> {
    fun findByRawEntityIdAndEntityType(rawEntityId: UUID, entityType: OparlType): EnrichTarget?

    @Query(
        """
            SELECT target
            FROM EnrichTarget target
            WHERE target.referenceCount > 0
              AND (target.scheduledAt <= :now OR target.scheduledAt IS NULL)
              AND target.jobId IS NULL
            ORDER BY CASE target.entityType
                WHEN nrw.openbert.backend.parldata.domain.enums.OparlType.SYSTEM THEN 0
                WHEN nrw.openbert.backend.parldata.domain.enums.OparlType.BODY THEN 1
                WHEN nrw.openbert.backend.parldata.domain.enums.OparlType.ORGANIZATION THEN 2
                WHEN nrw.openbert.backend.parldata.domain.enums.OparlType.PAPER THEN 2
                WHEN nrw.openbert.backend.parldata.domain.enums.OparlType.LEGISLATIVE_TERM THEN 2
                WHEN nrw.openbert.backend.parldata.domain.enums.OparlType.PERSON THEN 3
                WHEN nrw.openbert.backend.parldata.domain.enums.OparlType.MEETING THEN 4
                WHEN nrw.openbert.backend.parldata.domain.enums.OparlType.MEMBERSHIP THEN 4
                WHEN nrw.openbert.backend.parldata.domain.enums.OparlType.AGENDA_ITEM THEN 5
                WHEN nrw.openbert.backend.parldata.domain.enums.OparlType.FILE THEN 6
                WHEN nrw.openbert.backend.parldata.domain.enums.OparlType.CONSULTATION THEN 7
                WHEN nrw.openbert.backend.parldata.domain.enums.OparlType.LOCATION THEN 7
                ELSE 8
            END,
            target.referenceCount DESC,
            target.createdAt ASC
        """,
    )
    fun findDue(
        @Param("now") now: Instant,
        pageable: Pageable,
    ): List<EnrichTarget>
}
