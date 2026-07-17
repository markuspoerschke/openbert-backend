package nrw.openbert.backend.domain.oparl.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.domain.oparl.valueObject.OparlUrl
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.*

@MappedSuperclass
abstract class AbstractAgendaItem(systemId: UUID, url: OparlUrl) : AbstractOparlEntity(systemId, url) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/AgendaItem"
    }

    @Column
    private var number: String? = null

    @Column
    private var order: Int? = null

    @Column
    private var name: String? = null

    @Column
    private var public: Boolean? = null

    @Column
    private var result: String? = null

    @Column
    private var resolutionText: String? = null

    @Column
    private var start: Date? = null

    @Column
    private var end: Date? = null

    @Column
    private var license: String? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private var keyword: Array<String> = emptyArray()

    @Column
    private var web: String? = null

    fun applyValuesFrom(other: AbstractAgendaItem) {
        number = other.number
        order = other.order
        name = other.name
        public = other.public
        result = other.result
        resolutionText = other.resolutionText
        start = other.start
        end = other.end
        license = other.license
        keyword = other.keyword
        web = other.web
    }
}
