package nrw.openbert.backend.domain.oparl.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId
import nrw.openbert.backend.domain.oparl.valueObject.OparlUrl
import nrw.openbert.backend.infrastructure.oparl.persitence.converter.OparlUrlConverter
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.Date

@MappedSuperclass
abstract class AbstractPaper(id: OparlEntityId) : AbstractOparlEntity(id) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/Paper"
    }

    @Column
    private var name: String? = null

    @Column
    private var reference: String? = null

    @Column
    private var date: Date? = null

    @Column
    private var paperType: String? = null

    @Column
    private var license: String? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private var keyword: Array<String> = emptyArray()

    @Column
    private var web: String? = null

    fun applyValuesFrom(other: AbstractPaper) {
        name = other.name
        reference = other.reference
        date = other.date
        paperType = other.paperType
        license = other.license
        keyword = other.keyword
        web = other.web
    }
}
