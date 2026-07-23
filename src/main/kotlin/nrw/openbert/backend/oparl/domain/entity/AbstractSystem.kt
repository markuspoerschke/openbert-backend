package nrw.openbert.backend.oparl.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import nrw.openbert.backend.oparl.infrastructure.persitence.converter.OparlUrlConverter
import java.util.UUID

@MappedSuperclass
abstract class AbstractSystem(systemId: UUID, url: OparlUrl) : AbstractOparlEntity(systemId, url) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/System"
    }

    @Column
    private var oparlVersion: String = UNKNOWN

    @Column(columnDefinition = "text[]")
    private var otherOparlVersions: MutableList<String> = mutableListOf()

    @Convert(converter = OparlUrlConverter::class)
    @Column
    private var license: OparlUrl? = null

    @Column
    private var name: String = UNKNOWN

    @Column
    private var contactEmail: String? = null

    @Column
    private var contactName: String? = null

    @Convert(converter = OparlUrlConverter::class)
    @Column
    private var website: OparlUrl? = null

    @Convert(converter = OparlUrlConverter::class)
    @Column
    private var vendor: OparlUrl? = null

    @Convert(converter = OparlUrlConverter::class)
    @Column
    private var product: OparlUrl? = null

    @Column
    private var web: String? = null

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
