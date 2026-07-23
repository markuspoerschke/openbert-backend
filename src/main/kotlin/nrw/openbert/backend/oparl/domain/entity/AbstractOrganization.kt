package nrw.openbert.backend.oparl.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import nrw.openbert.backend.oparl.infrastructure.persitence.converter.OparlUrlConverter
import java.util.*

@MappedSuperclass
abstract class AbstractOrganization(resource: OparlResource) : AbstractOparlEntity(resource) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/Organization"
    }

    @Column
    private var name: String? = null

    @Column
    private var shortName: String? = null

    @Column(columnDefinition = "text[]")
    private var post: MutableList<String> = mutableListOf()

    @Column
    private var organizationType: String? = null

    @Column
    private var classification: String? = null

    @Column
    private var startDate: Date? = null

    @Column
    private var endDate: Date? = null

    @Convert(converter = OparlUrlConverter::class)
    @Column
    private var website: OparlUrl? = null

    @Column
    private var license: String? = null

    @Column(columnDefinition = "text[]")
    private var keyword: MutableList<String> = mutableListOf()

    @Column
    private var web: String? = null

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
