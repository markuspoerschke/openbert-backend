package nrw.openbert.backend.oparl.infrastructure.persistence.repository

import nrw.openbert.backend.TestcontainersConfiguration
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.adapter.outbound.persistence.repository.JpaOparlSearchRepository
import nrw.openbert.backend.parldata.application.port.model.SearchCriteria
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.transaction.annotation.Transactional
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@Transactional
class OparlSearchRepositoryTest(
    @Autowired private val searchRepository: JpaOparlSearchRepository,
    @Autowired private val jdbcTemplate: NamedParameterJdbcTemplate,
) {
    companion object {
        val SEARCHABLE_TYPES = listOf(
            OparlType.AGENDA_ITEM,
            OparlType.BODY,
            OparlType.CONSULTATION,
            OparlType.FILE,
            OparlType.LEGISLATIVE_TERM,
            OparlType.LOCATION,
            OparlType.MEETING,
            OparlType.MEMBERSHIP,
            OparlType.ORGANIZATION,
            OparlType.PAPER,
            OparlType.PERSON,
        )
    }

    private val page = PageRequest.of (0, 100)

    private fun search(
        term: String,
        bodies: List<String> = emptyList(),
        types: List<OparlType> = OparlType.entries.toList(),
    ) = searchRepository.search(SearchCriteria(term = term, bodies = bodies, types = types), page)

    @Test
    fun `excludes deleted records of every searchable type from data and counts`() {
        val initial = search("oparl-demo.example.org")
        assertEquals(SEARCHABLE_TYPES.toSet(), initial.content.map { it.entityType }.toSet())
        var expectedTotal = initial.totalElements

        OparlType.entries.forEach { type ->
            val table = "oparl_resolved_${type.name.lowercase(Locale.ROOT)}"
            jdbcTemplate.jdbcTemplate.update("UPDATE $table SET deleted = true")
            expectedTotal -= initial.content.count { it.entityType == type }

            val result = search("oparl-demo.example.org")
            assertFalse(result.content.any { it.entityType == type })
            assertEquals(expectedTotal, result.totalElements)
        }
        assertEquals(0, expectedTotal)
    }

    @Test
    fun `stored search text follows scalar and array updates`() {
        assertEquals(1, search("public transport").totalElements)
        jdbcTemplate.jdbcTemplate.update(
            """
                UPDATE oparl_resolved_file
                   SET text = 'Revised bicycle network', keyword = ARRAY['cycleways']
                 WHERE id = '22222222-2222-4222-a222-222222222222'
            """
        )

        assertEquals(0, search("public transport").totalElements)
        assertEquals(1, search("BICYCLE NETWORK").totalElements)
        assertEquals(1, search("cycleways").totalElements)

        jdbcTemplate.jdbcTemplate.update(
            "UPDATE oparl_resolved_file SET keyword = '{}' WHERE id = '22222222-2222-4222-a222-222222222222'"
        )
        assertEquals(0, search("cycleways").totalElements)
    }

    @Test
    fun `restoring and removing a tombstone updates search visibility`() {
        jdbcTemplate.jdbcTemplate.update(
            "UPDATE oparl_resolved_file SET deleted = false WHERE id = '66666666-6666-4666-a666-666666666666'"
        )
        assertEquals(2, search("public transport").totalElements)

        jdbcTemplate.jdbcTemplate.update(
            "DELETE FROM oparl_resolved_file WHERE id = '66666666-6666-4666-a666-666666666666'"
        )
        assertEquals(1, search("public transport").totalElements)
    }

    @Test
    fun `filters search results by body when a body list is provided`() {
        val firstBody = "903c26aa-dc8f-4d34-afd6-afdc5040dbd3"
        val secondBody = "ffffffff-ffff-4fff-afff-ffffffffffff"

        assertEquals(2, search("searchable").totalElements)
        assertEquals(0, search("searchable", bodies = listOf(firstBody)).totalElements)
        assertEquals(2, search("searchable", bodies = listOf(secondBody)).totalElements)
    }

    @Test
    fun `does not filter by body when body list is empty`() {
        val expected = search("searchable")
        val withEmptyBodyFilter = search("searchable", bodies = emptyList())

        assertEquals(expected.totalElements, withEmptyBodyFilter.totalElements)
        assertEquals(expected.content, withEmptyBodyFilter.content)
    }

    @Test
    fun `filters search results by type when a type list is provided`() {
        val filteredResult = search(
            term = "oparl-demo.example.org",
            types = listOf(OparlType.BODY, OparlType.FILE),
        )

        assertTrue(filteredResult.content.isNotEmpty())
        assertTrue(filteredResult.content.all { it.entityType == OparlType.BODY || it.entityType == OparlType.FILE })
        assertEquals(setOf(OparlType.BODY, OparlType.FILE), filteredResult.content.map { it.entityType }.toSet())
    }

    @Test
    fun `does not filter by type when type list is empty`() {
        val expected = search("oparl-demo.example.org")
        val withEmptyTypeFilter = search("oparl-demo.example.org", types = emptyList())

        assertEquals(expected.totalElements, withEmptyTypeFilter.totalElements)
        assertEquals(expected.content, withEmptyTypeFilter.content)
    }

    @Test
    fun `both page and count queries can use trigram indexes for every type`() {
        // Tiny fixtures normally favor sequential scans. Disable them locally to verify index eligibility.
        jdbcTemplate.jdbcTemplate.execute("SET LOCAL enable_seqscan = off")
        val parameters = mapOf(
            "pattern" to "%not-present%",
            "bodies" to "",
            "types" to OparlType.entries.joinToString(",") { it.name },
            "bodyFilterDisabled" to true,
            "typeFilterDisabled" to false,
            "limit" to 20,
            "offset" to 0,
        )

        listOf(JpaOparlSearchRepository.SEARCH_QUERY, JpaOparlSearchRepository.COUNT_QUERY).forEach { query ->
            val plan = jdbcTemplate.queryForList("EXPLAIN $query", parameters, String::class.java)
                .joinToString("\n")
            SEARCHABLE_TYPES.forEach { type ->
                val index = "oparl_resolved_${type.name.lowercase(Locale.ROOT)}_search_trgm_idx"
                assertTrue(plan.contains(index), "Expected $index in query plan:\n$plan")
            }
            assertFalse(plan.contains("Seq Scan"), plan)
        }
    }
}
