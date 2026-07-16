package nrw.openbert.backend.domain.oparl.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId
import nrw.openbert.backend.domain.oparl.valueObject.OparlUrl
import nrw.openbert.backend.infrastructure.oparl.persitence.converter.OparlUrlConverter
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@MappedSuperclass
abstract class AbstractConsultation(id: OparlEntityId) : AbstractOparlEntity(id) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/Consultation"
    }

    @Column
    private var authoritative: Boolean? = null

    @Column
    private var role: String? = null

    @Column
    private var license: String? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private var keyword: Array<String> = emptyArray()

    @Convert(converter = OparlUrlConverter::class)
    @Column
    private var web: OparlUrl? = null

    fun applyValuesFrom(other: AbstractConsultation) {
        authoritative = other.authoritative
        role = other.role
        license = other.license
        keyword = other.keyword
        web = other.web
    }
}
