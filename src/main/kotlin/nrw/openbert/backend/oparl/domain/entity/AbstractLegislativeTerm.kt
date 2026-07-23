package nrw.openbert.backend.oparl.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import nrw.openbert.backend.oparl.domain.valueObject.OparlUrl
import java.util.*

@MappedSuperclass
abstract class AbstractLegislativeTerm(resource: OparlResource) : AbstractOparlEntity(resource) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/LegislativeTerm"
    }

    @Column
    private var name: String? = null

    @Column
    private var startDate: Date? = null

    @Column
    private var endDate: Date? = null

    @Column
    private var license: String? = null

    @Column(columnDefinition = "text[]")
    private var keyword: MutableList<String> = mutableListOf()

    @Column
    private var web: String? = null

    fun applyValuesFrom(other: AbstractLegislativeTerm) {
        name = other.name
        startDate = other.startDate
        endDate = other.endDate
        license = other.license
        keyword = other.keyword
        web = other.web
    }
}
