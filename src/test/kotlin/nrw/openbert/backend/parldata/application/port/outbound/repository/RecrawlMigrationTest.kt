package nrw.openbert.backend.parldata.application.port.outbound.repository

import jakarta.persistence.EntityManager
import nrw.openbert.backend.TestcontainersConfiguration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.core.io.ByteArrayResource
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import javax.sql.DataSource
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@Transactional
class RecrawlMigrationTest(
    @Autowired private val dataSource: DataSource,
    @Autowired private val entityManager: EntityManager,
    @Autowired private val repository: CrawlTargetRepository,
) {
    @Test
    fun `makes all completed resources due while preserving pending crawl state`() {
        // Test fixtures are loaded after production migrations, so apply the migration to them explicitly.
        val previousTargets = repository.findAll()
        assertTrue(previousTargets.any { it.lastCrawledAt != null && it.referenceCount == 0 })
        assertTrue(previousTargets.any { it.lastCrawledAt != null && it.referenceCount > 1 })
        assertTrue(previousTargets.any { it.lastCrawledAt != null && it.entityType == null })
        assertTrue(previousTargets.any { it.lastCrawledAt == null && it.scheduledAt != null })

        entityManager.flush()
        // V14 still references the job_id column dropped by V17, so apply the equivalent without it.
        val sql =
            """
            UPDATE crawl_target
            SET last_crawled_at = NULL,
                reference_count = GREATEST(reference_count, 1),
                scheduled_at = NULL,
                modified_at = CURRENT_TIMESTAMP
            WHERE last_crawled_at IS NOT NULL;
            """.trimIndent()
        ResourceDatabasePopulator(ByteArrayResource(sql.toByteArray())).execute(dataSource)
        entityManager.clear()

        val dueIds = repository.findDue(Instant.now(), 100).map { it.id }.toSet()
        previousTargets.forEach { previous ->
            val current = repository.findById(previous.id).orElseThrow()
            assertEquals(previous.resource.system.id, current.resource.system.id)
            assertEquals(previous.resource.url, current.resource.url)
            assertEquals(previous.entityType, current.entityType)
            assertEquals(previous.createdAt, current.createdAt)

            if (previous.lastCrawledAt != null) {
                assertNull(current.lastCrawledAt)
                assertEquals(maxOf(previous.referenceCount, 1), current.referenceCount)
                assertNull(current.scheduledAt)
                assertTrue(current.modifiedAt > previous.modifiedAt)
                assertTrue(current.id in dueIds)
            } else {
                assertNull(current.lastCrawledAt)
                assertEquals(previous.referenceCount, current.referenceCount)
                assertEquals(previous.scheduledAt, current.scheduledAt)
                assertEquals(previous.modifiedAt, current.modifiedAt)
            }
        }
    }
}
