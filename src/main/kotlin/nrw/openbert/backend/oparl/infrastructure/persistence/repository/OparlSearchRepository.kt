package nrw.openbert.backend.oparl.infrastructure.persistence.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.util.UUID

enum class OparlSearchEntityType {
    SYSTEM,
    BODY,
    LEGISLATIVE_TERM,
    ORGANIZATION,
    PERSON,
    MEMBERSHIP,
    MEETING,
    AGENDA_ITEM,
    PAPER,
    CONSULTATION,
    FILE,
    LOCATION,
}

data class OparlSearchReference(
    val entityType: OparlSearchEntityType,
    val entityId: UUID,
)

@Repository
class OparlSearchRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
) {
    fun search(text: String, pageable: Pageable): Page<OparlSearchReference> {
        val parameters = mapOf(
            "text" to text,
            "limit" to pageable.pageSize,
            "offset" to pageable.offset,
        )
        val references = jdbcTemplate.query(SEARCH_QUERY, parameters) { resultSet, _ ->
            OparlSearchReference(
                entityType = OparlSearchEntityType.valueOf(resultSet.getString("entity_type")),
                entityId = resultSet.getObject("entity_id", UUID::class.java),
            )
        }
        val totalElements = jdbcTemplate.queryForObject(
            COUNT_QUERY,
            mapOf("text" to text),
            Long::class.java,
        ) ?: 0

        return PageImpl(references, pageable, totalElements)
    }

    companion object {
        private const val SEARCHABLE_ENTITIES = """
            SELECT 1 AS type_order, 'SYSTEM' AS entity_type, id AS entity_id,
                   concat_ws(' ', oparl_version, array_to_string(other_oparl_versions, ' '), license,
                       name, contact_email, contact_name, website, vendor, product, web, url) AS searchable_text
              FROM oparl_resolved_system
            UNION ALL
            SELECT 2, 'BODY', id,
                   concat_ws(' ', short_name, name, website, license, ags, rgs,
                       array_to_string(equivalent, ' '), contact_email, contact_name, classification,
                       array_to_string(keyword, ' '), web, url)
              FROM oparl_resolved_body
            UNION ALL
            SELECT 3, 'LEGISLATIVE_TERM', id,
                   concat_ws(' ', name, license, array_to_string(keyword, ' '), web, url)
              FROM oparl_resolved_legislative_term
            UNION ALL
            SELECT 4, 'ORGANIZATION', id,
                   concat_ws(' ', name, short_name, array_to_string(post, ' '), organization_type,
                       classification, website, license, array_to_string(keyword, ' '), web, url)
              FROM oparl_resolved_organization
            UNION ALL
            SELECT 5, 'PERSON', id,
                   concat_ws(' ', name, family_name, given_name, form_of_address, affix,
                       array_to_string(title, ' '), gender, array_to_string(phone, ' '),
                       array_to_string(email, ' '), array_to_string(status, ' '), life, life_source,
                       license, array_to_string(keyword, ' '), web, url)
              FROM oparl_resolved_person
            UNION ALL
            SELECT 6, 'MEMBERSHIP', id,
                   concat_ws(' ', role, license, array_to_string(keyword, ' '), web, url)
              FROM oparl_resolved_membership
            UNION ALL
            SELECT 7, 'MEETING', id,
                   concat_ws(' ', name, meeting_state, license, array_to_string(keyword, ' '), web, url)
              FROM oparl_resolved_meeting
            UNION ALL
            SELECT 8, 'AGENDA_ITEM', id,
                   concat_ws(' ', number, name, result, resolution_text, license,
                       array_to_string(keyword, ' '), web, url)
              FROM oparl_resolved_agenda_item
            UNION ALL
            SELECT 9, 'PAPER', id,
                   concat_ws(' ', name, reference, paper_type, license,
                       array_to_string(keyword, ' '), web, url)
              FROM oparl_resolved_paper
            UNION ALL
            SELECT 10, 'CONSULTATION', id,
                   concat_ws(' ', role, license, array_to_string(keyword, ' '), web, url)
              FROM oparl_resolved_consultation
            UNION ALL
            SELECT 11, 'FILE', id,
                   concat_ws(' ', name, file_name, mime_type, text, access_url, download_url,
                       external_service_url, file_license, license, array_to_string(keyword, ' '), web, url)
              FROM oparl_resolved_file
            UNION ALL
            SELECT 12, 'LOCATION', id,
                   concat_ws(' ', description, geojson::text, street_address, postal_code, locality,
                       sub_locality, room, license, array_to_string(keyword, ' '), web, url)
              FROM oparl_resolved_location
        """

        private const val MATCHED_ENTITIES = """
            SELECT type_order, entity_type, entity_id
              FROM searchable_entities
             WHERE position(lower(:text) in lower(searchable_text)) > 0
        """

        private const val SEARCH_QUERY = """
            WITH searchable_entities AS ($SEARCHABLE_ENTITIES),
                 matched_entities AS ($MATCHED_ENTITIES)
            SELECT entity_type, entity_id
              FROM matched_entities
             ORDER BY type_order, entity_id
             LIMIT :limit OFFSET :offset
        """

        private const val COUNT_QUERY = """
            WITH searchable_entities AS ($SEARCHABLE_ENTITIES),
                 matched_entities AS ($MATCHED_ENTITIES)
            SELECT count(*)
              FROM matched_entities
        """
    }
}
