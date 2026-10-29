package nrw.openbert.backend.parldata.application.port.outbound.repository

import nrw.openbert.backend.TestcontainersConfiguration
import nrw.openbert.backend.parldata.domain.entity.CrawlTarget
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@Transactional
class CrawlTargetRepositoryTest(
    @Autowired private val repository: CrawlTargetRepository,
) {
    @Test
    fun `only returns referenced targets that still need crawling`() {
        val unreferencedTarget = repository.findById(unreferencedTargetId()).orElseThrow()
        val referencedTarget =
            CrawlTarget
                .create(
                    unreferencedTarget.resource.copy(
                        url = OparlUrl("https://oparl-demo.example.org/oparl/v1.1/person/pending"),
                    ),
                ).increaseReferenceCounter()
        repository.saveAndFlush(referencedTarget)

        val dueTargetIds = repository.findDue(Instant.now(), limit = 100).map(CrawlTarget::id)

        assertTrue(referencedTarget.id in dueTargetIds)
        assertFalse(unreferencedTargetId() in dueTargetIds)
    }

    @Test
    fun `does not return failed targets`() {
        val failedTarget =
            CrawlTarget
                .create(
                    repository
                        .findById(unreferencedTargetId())
                        .orElseThrow()
                        .resource
                        .copy(url = OparlUrl("https://oparl-demo.example.org/oparl/v1.1/person/failed")),
                ).increaseReferenceCounter()
                .markAsFailed("upstream returned 404")
        repository.saveAndFlush(failedTarget)

        val dueTargetIds = repository.findDue(Instant.now(), limit = 100).map(CrawlTarget::id)

        assertFalse(failedTarget.id in dueTargetIds)
    }

    @Test
    fun `orders organization and person before membership regardless of reference count`() {
        val organizationId = UUID.fromString("30000000-0000-4000-8000-000000000001")
        val personId = UUID.fromString("30000000-0000-4000-8000-000000000002")
        val membershipId = UUID.fromString("30000000-0000-4000-8000-000000000003")

        val dependencyTargets =
            repository
                .findDue(Instant.now(), limit = 100)
                .map(CrawlTarget::id)
                .filter { it in setOf(organizationId, personId, membershipId) }

        assertEquals(listOf(organizationId, personId, membershipId), dependencyTargets)
    }

    private fun unreferencedTargetId(): UUID = UUID.fromString("eeeeeeee-eeee-4eee-aeee-eeeeeeeeeeee")
}
