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
abstract class AbstractBody(id: OparlEntityId) : AbstractOparlEntity(id) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/Body"
    }

    @Column
    private var shortName: String? = null

    @Column
    private var name: String = UNKNOWN

    @Convert(converter = OparlUrlConverter::class)
    @Column
    private var website: OparlUrl? = null

    @Convert(converter = OparlUrlConverter::class)
    @Column
    private var license: OparlUrl? = null

    @Column
    private var licenseValidSince: Date? = null

    @Column
    private var oparlSince: Date? = null

    @Column
    private var ags: String? = null

    @Column
    private var rgs: String? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private var equivalent: Array<String> = emptyArray()

    @Column
    private var contactEmail: String? = null

    @Column
    private var contactName: String? = null

    @Column
    private var classification: String? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private var keyword: Array<String> = emptyArray()

    @Column
    private var web: String? = null

    fun applyValuesFrom(other: AbstractBody) {
        shortName = other.shortName
        name = other.name
        website = other.website
        license = other.license
        licenseValidSince = other.licenseValidSince
        oparlSince = other.oparlSince
        ags = other.ags
        rgs = other.rgs
        equivalent = other.equivalent
        contactEmail = other.contactEmail
        contactName = other.contactName
        classification = other.classification
        keyword = other.keyword
        web = other.web
    }
}
