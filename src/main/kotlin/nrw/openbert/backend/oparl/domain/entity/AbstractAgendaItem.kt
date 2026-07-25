package nrw.openbert.backend.oparl.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import nrw.openbert.backend.oparl.domain.entity.embeddable.OparlResource
import java.util.*

@MappedSuperclass
abstract class AbstractAgendaItem(resource: OparlResource) : AbstractOparlEntity(resource) {
    companion object {
        const val TYPE = "https://schema.oparl.org/1.1/AgendaItem"
    }

    @Column
    var number: String? = null
        protected set

    @Column
    var order: Int? = null
        protected set

    @Column
    var name: String? = null
        protected set

    @Column
    var public: Boolean? = null
        protected set

    @Column
    var result: String? = null
        protected set

    @Column
    var resolutionText: String? = null
        protected set

    @Column
    var start: Date? = null
        protected set

    @Column
    var end: Date? = null
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
