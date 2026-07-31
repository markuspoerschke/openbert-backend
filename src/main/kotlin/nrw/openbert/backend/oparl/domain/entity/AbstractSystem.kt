package nrw.openbert.backend.oparl.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import nrw.openbert.backend.oparl.infrastructure.persistence.converter.OparlUrlConverter

@MappedSuperclass
abstract class AbstractSystem(resource: OparlResource) : AbstractOparlEntity(resource) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/System"
    }

    @Column
    var oparlVersion: String = UNKNOWN

    @Column(columnDefinition = "text[]")
    var otherOparlVersions: MutableList<String> = mutableListOf()

    @Convert(converter = OparlUrlConverter::class)
    @Column
    var license: OparlUrl? = null

    @Column
    var name: String = UNKNOWN

    @Column
    var contactEmail: String? = null

    @Column
    var contactName: String? = null

    @Convert(converter = OparlUrlConverter::class)
    @Column
    var website: OparlUrl? = null

    @Convert(converter = OparlUrlConverter::class)
    @Column
    var vendor: OparlUrl? = null

    @Convert(converter = OparlUrlConverter::class)
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
}
