package nrw.openbert.backend.oparl.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import nrw.openbert.backend.oparl.infrastructure.persistence.converter.OparlUrlConverter
import java.util.*

@MappedSuperclass
abstract class AbstractOrganization(resource: OparlResource) : AbstractOparlEntity(resource) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/Organization"
    }

    @Column
    var name: String? = null
        protected set

    @Column
    var shortName: String? = null
        protected set

    @Column(columnDefinition = "text[]")
    var post: MutableList<String> = mutableListOf()
        protected set

    @Column
    var organizationType: String? = null
        protected set

    @Column
    var classification: String? = null
        protected set

    @Column
    var startDate: Date? = null
        protected set

    @Column
    var endDate: Date? = null
        protected set

    @Convert(converter = OparlUrlConverter::class)
    @Column
    var website: OparlUrl? = null
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
}
