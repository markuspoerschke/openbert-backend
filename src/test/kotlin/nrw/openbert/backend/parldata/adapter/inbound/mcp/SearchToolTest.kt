package nrw.openbert.backend.parldata.adapter.inbound.mcp

import nrw.openbert.backend.TestcontainersConfiguration
import nrw.openbert.backend.parldata.domain.enums.OparlType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@Transactional
class SearchToolTest {
    @Autowired
    private lateinit var searchTool: SearchTool

    @Test
    fun `searches with body filter`() {
        val results =
            searchTool.search(
                term = "haushalt",
                bodies = listOf("903c26aa-dc8f-4d34-afd6-afdc5040dbd3"),
            )

        assert(results.data.isNotEmpty())
    }

    @Test
    fun `searches with type filter`() {
        val results =
            searchTool.search(
                term = "haushalt",
                types = listOf(OparlType.ORGANIZATION),
            )

        assert(results.data.isNotEmpty())
        val firstResult = results.data[0]
        if (firstResult is Map<*, *>) {
            assertEquals(OparlType.ORGANIZATION.url, firstResult["type"])
        }
    }

    @Test
    fun `searches with combined filters`() {
        val results =
            searchTool.search(
                term = "searchable",
                bodies = listOf("ffffffff-ffff-4fff-afff-ffffffffffff"),
                types = listOf(OparlType.FILE),
            )

        assert(results.data.isNotEmpty())
        val firstResult = results.data[0]
        if (firstResult is Map<*, *>) {
            assertEquals(OparlType.FILE.url, firstResult["type"])
        }
    }

    @Test
    fun `handles empty type list gracefully`() {
        val results =
            searchTool.search(
                term = "searchable",
                types = emptyList(),
            )

        assert(results.data.isNotEmpty()) // Should fallback to all types
    }

    @Test
    fun `allows search without term`() {
        val results =
            searchTool.search(
                bodies = listOf("903c26aa-dc8f-4d34-afd6-afdc5040dbd3"),
                types = listOf(OparlType.ORGANIZATION),
            )

        assert(results.data.isNotEmpty())
        val firstResult = results.data[0]
        if (firstResult is Map<*, *>) {
            assertEquals(OparlType.ORGANIZATION.url, firstResult["type"])
        }
    }
}
