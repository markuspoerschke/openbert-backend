package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.parldata.domain.entity.CrawlTarget
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.UUID

interface CrawlTargetRepository : JpaRepository<CrawlTarget, UUID> {
    @Query(
        value = """
            SELECT *
            FROM crawl_target
            WHERE reference_count > 0
              AND (scheduled_at <= ?1 OR scheduled_at IS NULL)
              AND job_id IS NULL
              AND last_crawled_at IS NULL
              AND failed_at IS NULL
            ORDER BY CASE entity_type
                WHEN 'SYSTEM' THEN 0
                WHEN 'BODY' THEN 1
                WHEN 'ORGANIZATION' THEN 2
                WHEN 'PAPER' THEN 2
                WHEN 'LEGISLATIVE_TERM' THEN 2
                WHEN 'PERSON' THEN 3
                WHEN 'MEETING' THEN 4
                WHEN 'MEMBERSHIP' THEN 4
                WHEN 'FILE' THEN 5
                WHEN 'AGENDA_ITEM' THEN 6
                WHEN 'CONSULTATION' THEN 7
                WHEN 'LOCATION' THEN 7
                ELSE 8
            END,
            reference_count DESC,
            created_at ASC
            LIMIT ?2
        """,
        nativeQuery = true,
    )
    fun findDue(
        now: Instant,
        limit: Int,
    ): List<CrawlTarget>

    fun findByResource(resource: OparlResource): CrawlTarget?
}
