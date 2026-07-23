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
        protected set

    @Column
    var familyName: String? = null
        protected set

    @Column
    var givenName: String? = null
        protected set

    @Column
    var formOfAddress: String? = null
        protected set

    @Column
    var affix: String? = null
        protected set

    @Column(columnDefinition = "text[]")
    var title: MutableList<String> = mutableListOf()
        protected set

    @Column
    var gender: String? = null
        protected set

    @Column(columnDefinition = "text[]")
    var phone: MutableList<String> = mutableListOf()
        protected set

    @Column(columnDefinition = "text[]")
    var email: MutableList<String> = mutableListOf()
        protected set

    @Column(columnDefinition = "text[]")
    var status: MutableList<String> = mutableListOf()
        protected set

    @Column
    var life: String? = null
        protected set

    @Column
    var lifeSource: String? = null
        protected set

    @Column
    var license: String? = null
        protected set

    @Column(columnDefinition = "text[]")
    var keyword: MutableList<String> = mutableListOf()
        protected set

    @Column
    var web: String? = null
        protected set

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
