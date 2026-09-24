package nrw.openbert.backend.parldata.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.parldata.domain.valueObject.OparlUrl
import nrw.openbert.backend.parldata.adapter.outbound.persistence.converter.OparlUrlConverter
import nrw.openbert.backend.parldata.domain.enums.OparlType
import java.util.*

@MappedSuperclass
abstract class AbstractOrganization(resource: OparlResource) : AbstractOparlEntity(resource) {
    @Column(length = 500)
    var name: String? = null

    @Column
    var shortName: String? = null

    @Column(columnDefinition = "text[]")
    var post: MutableList<String> = mutableListOf()

    @Column
    var organizationType: String? = null

    @Column
    var classification: String? = null

    @Column
    var startDate: Date? = null

    @Column
    var endDate: Date? = null

    @Convert(converter = OparlUrlConverter::class)
    @Column
    var website: OparlUrl? = null

    @Column
    var license: String? = null

    @Column(columnDefinition = "text[]")
    var keyword: MutableList<String> = mutableListOf()

    @Column
    var web: String? = null

    fun applyValuesFrom(other: AbstractOrganization) {
        name = other.name
        shortName = other.shortName
        post = other.post
        organizationType = other.organizationType
        classification = other.classification
        startDate = other.startDate
        endDate = other.endDate
        website = other.website
        license = other.license
        keyword = other.keyword
        web = other.web
    }

    override fun getType() = OparlType.ORGANIZATION
}
