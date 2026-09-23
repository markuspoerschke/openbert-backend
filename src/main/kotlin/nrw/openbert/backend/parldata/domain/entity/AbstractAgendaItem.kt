package nrw.openbert.backend.parldata.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.parldata.domain.entity.embeddable.OparlResource
import java.util.*

@MappedSuperclass
abstract class AbstractAgendaItem(resource: OparlResource) : AbstractOparlEntity(resource) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/AgendaItem"
    }

    @Column
    var number: String? = null

    @Column(name = "\"order\"")
    var order: Int? = null

    @Column(length = 500)
    var name: String? = null

    @Column
    var public: Boolean? = null

    @Column
    var result: String? = null

    @Column
    var resolutionText: String? = null

    @Column
    var start: Date? = null

    @Column(name = "\"end\"")
    var end: Date? = null

    @Column
    var license: String? = null

    @Column(columnDefinition = "text[]")
    var keyword: MutableList<String> = mutableListOf()

    @Column
    var web: String? = null

    fun applyValuesFrom(other: AbstractAgendaItem) {
        number = other.number
        order = other.order
        name = other.name
        public = other.public
        result = other.result
        resolutionText = other.resolutionText
        start = other.start
        end = other.end
        license = other.license
        keyword = other.keyword
        web = other.web
    }
}
