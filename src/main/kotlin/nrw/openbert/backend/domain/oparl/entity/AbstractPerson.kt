package nrw.openbert.backend.domain.oparl.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.domain.oparl.valueObject.OparlEntityId
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@MappedSuperclass
abstract class AbstractPerson(id: OparlEntityId) : AbstractOparlEntity(id) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/Person"
    }

    @Column
    private var name: String? = null

    @Column
    private var familyName: String? = null

    @Column
    private var givenName: String? = null

    @Column
    private var formOfAddress: String? = null

    @Column
    private var affix: String? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private var title: Array<String> = emptyArray()

    @Column
    private var gender: String? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private var phone: Array<String> = emptyArray()

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private var email: Array<String> = emptyArray()

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private var status: Array<String> = emptyArray()

    @Column
    private var life: String? = null

    @Column
    private var lifeSource: String? = null

    @Column
    private var license: String? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private var keyword: Array<String> = emptyArray()

    @Column
    private var web: String? = null

    fun applyValuesFrom(other: AbstractPerson) {
        name = other.name
        familyName = other.familyName
        givenName = other.givenName
        formOfAddress = other.formOfAddress
        affix = other.affix
        title = other.title
        gender = other.gender
        phone = other.phone
        email = other.email
        status = other.status
        life = other.life
        lifeSource = other.lifeSource
        license = other.license
        keyword = other.keyword
        web = other.web
    }
}
