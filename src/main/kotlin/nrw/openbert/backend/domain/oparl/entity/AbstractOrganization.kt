package nrw.openbert.backend.domain.oparl.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId
import nrw.openbert.backend.domain.oparl.valueObject.OparlUrl
import nrw.openbert.backend.infrastructure.oparl.persitence.converter.OparlUrlConverter
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.*

@MappedSuperclass
abstract class AbstractOrganization(id: OparlEntityId) : AbstractOparlEntity(id) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/Organization"
    }

    @Column
    private var name: String? = null

    @Column
    private var shortName: String? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private var post: Array<String> = emptyArray()

    @Column
    private var organizationType: String? = null

    @Column
    private var classification: String? = null

    @Column
    private var startDate: Date? = null

    @Column
    private var endDate: Date? = null

    @Convert(converter = OparlUrlConverter::class)
    @Column
    private var website: OparlUrl? = null

    @Column
    private var license: String? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private var keyword: Array<String> = emptyArray()

    @Column
    private var web: String? = null

    fun applyValuesFrom(other: AbstractOrganization) {
        name = other.name
        shortName = other.shortName
        post = other.post
        organizationType = other.organizationType
        classification = other.classification
        startDate = other.startDate
        endDate = other.endDate
        website = other.website
        license = other.license
        keyword = other.keyword
        web = other.web
    }
}
