package nrw.openbert.backend.oparl.infrastructure.persistence.repository

import nrw.openbert.backend.TestcontainersConfiguration
import nrw.openbert.backend.parldata.domain.enums.OparlType
import nrw.openbert.backend.parldata.adapter.outbound.persistence.repository.JpaOparlSearchRepository
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

    @Test
    fun `excludes deleted records of every searchable type from data and counts`() {
        val initial = searchRepository.search("oparl-demo.example.org", page)
        assertEquals(SEARCHABLE_TYPES.toSet(), initial.content.map { it.entityType }.toSet())
        var expectedTotal = initial.totalElements

        OparlType.entries.forEach { type ->
            val table = "oparl_resolved_${type.name.lowercase(Locale.ROOT)}"
            jdbcTemplate.jdbcTemplate.update("UPDATE $table SET deleted = true")
            expectedTotal -= initial.content.count { it.entityType == type }

            val result = searchRepository.search("oparl-demo.example.org", page)
            assertFalse(result.content.any { it.entityType == type })
            assertEquals(expectedTotal, result.totalElements)
        }
        assertEquals(0, expectedTotal)
    }

    @Test
    fun `stored search text follows scalar and array updates`() {
        assertEquals(1, searchRepository.search("public transport", page).totalElements)
        jdbcTemplate.jdbcTemplate.update(
            """
                UPDATE oparl_resolved_file
                   SET text = 'Revised bicycle network', keyword = ARRAY['cycleways']
                 WHERE id = '22222222-2222-4222-a222-222222222222'
            """
        )

        assertEquals(0, searchRepository.search("public transport", page).totalElements)
        assertEquals(1, searchRepository.search("BICYCLE NETWORK", page).totalElements)
        assertEquals(1, searchRepository.search("cycleways", page).totalElements)

        jdbcTemplate.jdbcTemplate.update(
            "UPDATE oparl_resolved_file SET keyword = '{}' WHERE id = '22222222-2222-4222-a222-222222222222'"
        )
        assertEquals(0, searchRepository.search("cycleways", page).totalElements)
    }

    @Test
    fun `restoring and removing a tombstone updates search visibility`() {
        jdbcTemplate.jdbcTemplate.update(
            "UPDATE oparl_resolved_file SET deleted = false WHERE id = '66666666-6666-4666-a666-666666666666'"
        )
        assertEquals(2, searchRepository.search("public transport", page).totalElements)

        jdbcTemplate.jdbcTemplate.update(
            "DELETE FROM oparl_resolved_file WHERE id = '66666666-6666-4666-a666-666666666666'"
        )
        assertEquals(1, searchRepository.search("public transport", page).totalElements)
    }

    @Test
    fun `both page and count queries can use trigram indexes for every type`() {
        // Tiny fixtures normally favor sequential scans. Disable them locally to verify index eligibility.
        jdbcTemplate.jdbcTemplate.execute("SET LOCAL enable_seqscan = off")
        val parameters = mapOf("pattern" to "%not-present%", "limit" to 20, "offset" to 0)

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
