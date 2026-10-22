package nrw.openbert.backend.parldata.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import nrw.openbert.backend.parldata.domain.enums.OparlType

@MappedSuperclass
abstract class AbstractSystem(resource: OparlResource) : AbstractOparlEntity(resource) {
    @Column
    var oparlVersion: String = UNKNOWN

    @Column(columnDefinition = "text[]")
    var otherOparlVersions: MutableList<String> = mutableListOf()

    @Column
    var license: OparlUrl? = null

    @Column
    var name: String = UNKNOWN

    @Column
    var contactEmail: String? = null

    @Column
    var contactName: String? = null

    @Column
    var website: OparlUrl? = null

    @Column
    var vendor: OparlUrl? = null

    @Column
    var product: OparlUrl? = null

    @Column
    var web: String? = null

    fun applyValuesFrom(other: AbstractSystem) {
        oparlVersion = other.oparlVersion
        otherOparlVersions = other.otherOparlVersions
        license = other.license
        name = other.name
        contactEmail = other.contactEmail
        contactName = other.contactName
        website = other.website
        vendor = other.vendor
        product = other.product
        web = other.web
    }

    override fun getType() = OparlType.SYSTEM
}
