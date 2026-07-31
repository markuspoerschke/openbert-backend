package nrw.openbert.backend.crawl.domain.repository

import nrw.openbert.backend.crawl.domain.entity.CrawlTarget
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
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
        """,
        nativeQuery = true
    )
    fun findDue(now: Instant): List<CrawlTarget>

    fun findByResource(resource: OparlResource): CrawlTarget?
}
