package nrw.openbert.backend.parldata.adapter.outbound.persistence.repository

import nrw.openbert.backend.parldata.domain.valueObject.OparlSearchReference
import nrw.openbert.backend.parldata.application.port.outbound.repository.OparlSearchRepository
import nrw.openbert.backend.parldata.domain.enums.OparlType
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class JpaOparlSearchRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
): OparlSearchRepository {
    override fun search(text: String, pageable: Pageable): Page<OparlSearchReference> {
        val parameters = mapOf(
            "pattern" to "%${text.replace("!", "!!").replace("%", "!%").replace("_", "!_")}%",
            "limit" to pageable.pageSize,
            "offset" to pageable.offset,
        )
        val references = jdbcTemplate.query(SEARCH_QUERY, parameters) { resultSet, _ ->
            OparlSearchReference(
                entityType = OparlType.valueOf(resultSet.getString("entity_type")),
                entityId = resultSet.getObject("entity_id", UUID::class.java),
            )
        }
        val totalElements = jdbcTemplate.queryForObject(
            COUNT_QUERY,
            parameters,
            Long::class.java,
        ) ?: 0

        return PageImpl(references, pageable, totalElements)
    }

    companion object {
        // Source systems have no individual local endpoint and must not appear as the local system.
        private const val MATCHED_ENTITIES = """
            SELECT 1 AS type_order, 'BODY' AS entity_type, id AS entity_id
              FROM oparl_resolved_body
             WHERE NOT deleted AND searchable_text LIKE lower(:pattern) ESCAPE '!'
            UNION ALL
            SELECT 2 AS type_order, 'LEGISLATIVE_TERM' AS entity_type, id AS entity_id
              FROM oparl_resolved_legislative_term
             WHERE NOT deleted AND searchable_text LIKE lower(:pattern) ESCAPE '!'
            UNION ALL
            SELECT 3 AS type_order, 'ORGANIZATION' AS entity_type, id AS entity_id
              FROM oparl_resolved_organization
             WHERE NOT deleted AND searchable_text LIKE lower(:pattern) ESCAPE '!'
            UNION ALL
            SELECT 4 AS type_order, 'PERSON' AS entity_type, id AS entity_id
              FROM oparl_resolved_person
             WHERE NOT deleted AND searchable_text LIKE lower(:pattern) ESCAPE '!'
            UNION ALL
            SELECT 5 AS type_order, 'MEMBERSHIP' AS entity_type, id AS entity_id
              FROM oparl_resolved_membership
             WHERE NOT deleted AND searchable_text LIKE lower(:pattern) ESCAPE '!'
            UNION ALL
            SELECT 6 AS type_order, 'MEETING' AS entity_type, id AS entity_id
              FROM oparl_resolved_meeting
             WHERE NOT deleted AND searchable_text LIKE lower(:pattern) ESCAPE '!'
            UNION ALL
            SELECT 7 AS type_order, 'AGENDA_ITEM' AS entity_type, id AS entity_id
              FROM oparl_resolved_agenda_item
             WHERE NOT deleted AND searchable_text LIKE lower(:pattern) ESCAPE '!'
            UNION ALL
            SELECT 8 AS type_order, 'PAPER' AS entity_type, id AS entity_id
              FROM oparl_resolved_paper
             WHERE NOT deleted AND searchable_text LIKE lower(:pattern) ESCAPE '!'
            UNION ALL
            SELECT 9 AS type_order, 'CONSULTATION' AS entity_type, id AS entity_id
              FROM oparl_resolved_consultation
             WHERE NOT deleted AND searchable_text LIKE lower(:pattern) ESCAPE '!'
            UNION ALL
            SELECT 10 AS type_order, 'FILE' AS entity_type, id AS entity_id
              FROM oparl_resolved_file
             WHERE NOT deleted AND searchable_text LIKE lower(:pattern) ESCAPE '!'
            UNION ALL
            SELECT 11 AS type_order, 'LOCATION' AS entity_type, id AS entity_id
              FROM oparl_resolved_location
             WHERE NOT deleted AND searchable_text LIKE lower(:pattern) ESCAPE '!'
        """

        internal const val SEARCH_QUERY = """
            WITH matched_entities AS ($MATCHED_ENTITIES)
            SELECT entity_type, entity_id
              FROM matched_entities
             ORDER BY type_order, entity_id
             LIMIT :limit OFFSET :offset
        """

        internal const val COUNT_QUERY = """
            WITH matched_entities AS ($MATCHED_ENTITIES)
            SELECT count(*)
              FROM matched_entities
        """
    }
}
