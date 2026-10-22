package nrw.openbert.backend.crawl.application

import jakarta.persistence.EntityManager
import nrw.openbert.backend.TestcontainersConfiguration
import nrw.openbert.backend.parldata.application.port.outbound.repository.CrawlTargetRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.core.io.ClassPathResource
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
        assertTrue(previousTargets.any { it.lastCrawledAt == null && it.jobId != null })

        entityManager.flush()
        ResourceDatabasePopulator(
            ClassPathResource("db/migration/V14__recrawl_previously_crawled_resources.sql"),
        ).execute(dataSource)
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
                assertNull(current.jobId)
                assertTrue(current.modifiedAt > previous.modifiedAt)
                assertTrue(current.id in dueIds)
            } else {
                assertNull(current.lastCrawledAt)
                assertEquals(previous.referenceCount, current.referenceCount)
                assertEquals(previous.scheduledAt, current.scheduledAt)
                assertEquals(previous.jobId, current.jobId)
                assertEquals(previous.modifiedAt, current.modifiedAt)
            }
        }
    }
}
