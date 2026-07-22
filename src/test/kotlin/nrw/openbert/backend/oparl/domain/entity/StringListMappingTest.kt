package nrw.openbert.backend.oparl.domain.entity

import jakarta.persistence.Column
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StringListMappingTest {

    @Test
    fun `maps OParl string lists to PostgreSQL text arrays`() {
        val stringListFields = mapOf(
            AbstractAgendaItem::class.java to listOf("keyword"),
            AbstractBody::class.java to listOf("keyword"),
            AbstractConsultation::class.java to listOf("keyword"),
            AbstractFile::class.java to listOf("keyword"),
            AbstractLegislativeTerm::class.java to listOf("keyword"),
            AbstractLocation::class.java to listOf("keyword"),
            AbstractMeeting::class.java to listOf("keyword"),
            AbstractMembership::class.java to listOf("keyword"),
            AbstractOrganization::class.java to listOf("post", "keyword"),
            AbstractPaper::class.java to listOf("keyword"),
            AbstractPerson::class.java to listOf("title", "phone", "email", "status", "keyword"),
            AbstractSystem::class.java to listOf("otherOparlVersions"),
        )

        stringListFields.forEach { (entityClass, fieldNames) ->
            fieldNames.forEach { fieldName ->
                val field = entityClass.getDeclaredField(fieldName)

                assertEquals(List::class.java, field.type, "${entityClass.simpleName}.$fieldName Kotlin type")
                assertEquals(
                    "text[]",
                    field.getAnnotation(Column::class.java).columnDefinition,
                    "${entityClass.simpleName}.$fieldName SQL type",
                )
            }
        }
    }
}