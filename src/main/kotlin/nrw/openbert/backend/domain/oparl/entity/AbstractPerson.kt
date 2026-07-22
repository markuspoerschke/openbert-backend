package nrw.openbert.backend.domain.oparl.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.domain.oparl.valueObject.OparlUrl
import java.util.UUID

@MappedSuperclass
abstract class AbstractPerson(systemId: UUID, url: OparlUrl) : AbstractOparlEntity(systemId, url) {
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

    @Column(columnDefinition = "text[]")
    private var title: MutableList<String> = mutableListOf()

    @Column
    private var gender: String? = null

    @Column(columnDefinition = "text[]")
    private var phone: MutableList<String> = mutableListOf()

    @Column(columnDefinition = "text[]")
    private var email: MutableList<String> = mutableListOf()

    @Column(columnDefinition = "text[]")
    private var status: MutableList<String> = mutableListOf()

    @Column
    private var life: String? = null

    @Column
    private var lifeSource: String? = null

    @Column
    private var license: String? = null

    @Column(columnDefinition = "text[]")
    private var keyword: MutableList<String> = mutableListOf()

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
