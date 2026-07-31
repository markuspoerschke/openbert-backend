package nrw.openbert.backend.crawl.domain.repository

import nrw.openbert.backend.TestcontainersConfiguration
import nrw.openbert.backend.crawl.domain.entity.CrawlTarget
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@Transactional
class CrawlTargetRepositoryTest(
    @Autowired private val repository: CrawlTargetRepository,
) {
    @Test
    fun `only returns referenced targets that still need indexing`() {
        val indexedTargetId = UUID.fromString("eeeeeeee-eeee-4eee-aeee-eeeeeeeeeeee")
        val indexedTarget = repository.findById(indexedTargetId).orElseThrow()
        val referencedTarget = CrawlTarget.create(
            indexedTarget.resource.copy(
                url = OparlUrl("https://oparl-demo.example.org/oparl/v1.1/person/pending"),
            ),
        ).increaseReferenceCounter()
        repository.saveAndFlush(referencedTarget)

        val dueTargetIds = repository.findDue(Instant.now()).map(CrawlTarget::id)

        assertTrue(referencedTarget.id in dueTargetIds)
        assertFalse(indexedTargetId in dueTargetIds)
    }
}
