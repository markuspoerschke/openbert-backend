package nrw.openbert.backend.domain.oparl.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.domain.oparl.valueObject.OparlUrl
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.*

@MappedSuperclass
abstract class AbstractLegislativeTerm(systemId: UUID, url: OparlUrl) : AbstractOparlEntity(systemId, url) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/LegislativeTerm"
    }

    @Column
    private var name: String? = null

    @Column
    private var startDate: Date? = null

    @Column
    private var endDate: Date? = null

    @Column
    private var license: String? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private var keyword: Array<String> = emptyArray()

    @Column
    private var web: String? = null

    fun applyValuesFrom(other: AbstractLegislativeTerm) {
        name = other.name
        startDate = other.startDate
        endDate = other.endDate
        license = other.license
        keyword = other.keyword
        web = other.web
    }
}
