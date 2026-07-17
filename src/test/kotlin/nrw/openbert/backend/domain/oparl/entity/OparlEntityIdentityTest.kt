package nrw.openbert.backend.domain.oparl.entity

import jakarta.persistence.Id
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class OparlEntityIdentityTest {

    @Test
    fun `uses UUID as entity primary key`() {
        val id = AbstractOparlEntity::class.java.getDeclaredField("id")

        assertEquals(UUID::class.java, id.type)
        assertNotNull(id.getAnnotation(Id::class.java))
        assertEquals(UUID::class.java, AbstractOparlEntity::class.java.getDeclaredField("systemId").type)
        assertNotNull(AbstractOparlEntity::class.java.getDeclaredField("url"))
    }

    @Test
    fun `uses system UUID and URL as unique key instead of primary key`() {
        val migration = javaClass.classLoader
            .getResourceAsStream("db/migration/V1__init.sql")
            ?.bufferedReader()
            ?.use { it.readText() }
        assertNotNull(migration)

        OPARL_ENTITY_TABLES.forEach { table ->
            val tableDefinition = Regex("CREATE TABLE $table\\s*\\((.*?)\\);", RegexOption.DOT_MATCHES_ALL)
                .find(migration)
                ?.groupValues
                ?.get(1)
            assertNotNull(tableDefinition, "Missing table definition for $table")
            assertTrue(
                Regex("(?m)^\\s*id\\s+UUID\\s+NOT NULL").containsMatchIn(tableDefinition),
                "$table must have a non-null UUID id column"
            )
            assertTrue(
                Regex("ALTER TABLE $table\\s+ADD CONSTRAINT \\S+ PRIMARY KEY \\(id\\);")
                    .containsMatchIn(migration),
                "$table must use id as its primary key"
            )
            assertTrue(
                Regex("ALTER TABLE $table\\s+ADD CONSTRAINT \\S+ UNIQUE \\(system_id, url\\);")
                    .containsMatchIn(migration),
                "$table must have a unique key on system_id and url"
            )
        }
    }

    companion object {
        private val OPARL_ENTITY_TABLES = listOf(
            "agenda_item",
            "body",
            "consultation",
            "file",
            "legislative_term",
            "location",
            "meeting",
            "membership",
            "organization",
            "paper",
            "person",
            "system"
        ).flatMap { entity -> listOf("oparl_raw_$entity", "oparl_resolved_$entity") }
    }
}