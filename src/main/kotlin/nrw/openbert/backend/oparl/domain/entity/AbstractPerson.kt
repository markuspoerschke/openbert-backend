package nrw.openbert.backend.oparl.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource

@MappedSuperclass
abstract class AbstractPerson(resource: OparlResource) : AbstractOparlEntity(resource) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/Person"
    }

    @Column
    var name: String? = null

    @Column
    var familyName: String? = null

    @Column
    var givenName: String? = null

    @Column
    var formOfAddress: String? = null

    @Column
    var affix: String? = null

    @Column(columnDefinition = "text[]")
    var title: MutableList<String> = mutableListOf()

    @Column
    var gender: String? = null

    @Column(columnDefinition = "text[]")
    var phone: MutableList<String> = mutableListOf()

    @Column(columnDefinition = "text[]")
    var email: MutableList<String> = mutableListOf()

    @Column(columnDefinition = "text[]")
    var status: MutableList<String> = mutableListOf()

    @Column
    var life: String? = null

    @Column
    var lifeSource: String? = null

    @Column
    var license: String? = null

    @Column(columnDefinition = "text[]")
    var keyword: MutableList<String> = mutableListOf()

    @Column
    var web: String? = null

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
