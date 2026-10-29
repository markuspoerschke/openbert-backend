package nrw.openbert.backend.parldata.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.enums.OparlType
import java.util.Date

@MappedSuperclass
abstract class AbstractBody(
    resource: OparlResource,
) : AbstractOparlEntity(resource) {
    @Column
    var shortName: String? = null

    @Column(length = 500)
    var name: String = UNKNOWN

    @Column
    var website: String? = null

    @Column
    var license: String? = null

    @Column
    var licenseValidSince: Date? = null

    @Column
    var oparlSince: Date? = null

    @Column
    var ags: String? = null

    @Column
    var rgs: String? = null

    @Column(columnDefinition = "text[]")
    var equivalent: MutableList<String> = mutableListOf<String>()

    @Column
    var contactEmail: String? = null

    @Column
    var contactName: String? = null

    @Column
    var classification: String? = null

    @Column(columnDefinition = "text[]")
    var keyword: MutableList<String> = mutableListOf()

    @Column
    var web: String? = null

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

    override fun getType() = OparlType.BODY
}
