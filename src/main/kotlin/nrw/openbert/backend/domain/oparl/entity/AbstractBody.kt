package nrw.openbert.backend.domain.oparl.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.domain.oparl.valueObject.OparlUrl
import nrw.openbert.backend.infrastructure.oparl.persitence.converter.OparlUrlConverter
import java.util.*

@MappedSuperclass
abstract class AbstractBody(systemId: UUID, url: OparlUrl) : AbstractOparlEntity(systemId, url) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/Body"
    }

    @Column
    var shortName: String? = null
        protected set

    @Column
    var name: String = UNKNOWN
        protected set

    @Convert(converter = OparlUrlConverter::class)
    @Column
    var website: OparlUrl? = null
        protected set

    @Convert(converter = OparlUrlConverter::class)
    @Column
    var license: OparlUrl? = null
        protected set

    @Column
    var licenseValidSince: Date? = null
        protected set

    @Column
    var oparlSince: Date? = null
        protected set

    @Column
    var ags: String? = null
        protected set

    @Column
    var rgs: String? = null
        protected set

    @Column(columnDefinition = "text[]")
    var equivalent: MutableList<String> = mutableListOf<String>()
        protected set

    @Column
    var contactEmail: String? = null
        protected set

    @Column
    var contactName: String? = null
        protected set

    @Column
    var classification: String? = null
        protected set

    @Column(columnDefinition = "text[]")
    var keyword: MutableList<String> = mutableListOf()
        protected set

    @Column
    var web: String? = null
        protected set

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
